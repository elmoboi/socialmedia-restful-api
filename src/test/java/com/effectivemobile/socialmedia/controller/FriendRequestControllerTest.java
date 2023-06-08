package com.effectivemobile.socialmedia.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestDTO;
import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestResponseDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class FriendRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserEntityService userEntityService;


    @Test
    @WithAnonymousUser
    void testSendFriendRequest_whenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/users/{userEntityId}/friend-request", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testAcceptFriendRequest_whenUnauthorized() throws Exception {
        mockMvc.perform(put("/api/users/{userEntityId}/accept-friend-request/{userEntitySenderId}", 1, 2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testGetAllFriendRequest_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/{userEntityId}/friend-requests", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testDeleteFriendRequest_whenUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{userEntityId}/delete-friend-request/{userEntityReceiverId}", 1, 2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testUnsubscribeFriendRequest_whenUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{userEntityId}/unsubscribe-request/{userEntityReceiverId}", 1, 2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testGetUserFriendsRequest_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/{userEntityId}/friends", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testCorrectSendFriendRequest() throws Exception {
        Long userEntityId_first = 1L;

        FriendRequestDTO friendRequestDTOGood = new FriendRequestDTO();
        friendRequestDTOGood.setSenderId(1L);
        friendRequestDTOGood.setReceiverId(2L);

        mockMvc.perform(post("/api/users/{userEntityId}/friend-request", userEntityId_first)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendRequestDTOGood)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success send friend request"));

        verify(userEntityService, times(1)).sendFriendRequest(userEntityId_first, friendRequestDTOGood);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testWrongSendFriendRequest() throws Exception {
        Long userEntityId_first = 1L;

        FriendRequestDTO friendRequestDTOBad = new FriendRequestDTO();
        friendRequestDTOBad.setSenderId(2L);
        friendRequestDTOBad.setReceiverId(1L);

        mockMvc.perform(post("/api/users/{userEntityId}/friend-request", userEntityId_first)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendRequestDTOBad)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User IDs didn't equal"));

        verify(userEntityService, times(0)).sendFriendRequest(userEntityId_first, friendRequestDTOBad);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testAcceptFriendRequest() throws Exception {
        Long userEntityId = 1L;
        Long userEntitySenderId = 2L;
        String choice = "accept";

        mockMvc.perform(put("/api/users/{userEntityId}/accept-friend-request/{userEntitySenderId}", userEntityId, userEntitySenderId)
                        .param("choice", choice))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request accepted"));

        verify(userEntityService, times(1)).acceptFriendRequest(userEntityId, userEntitySenderId);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testDeclineFriendRequest() throws Exception {
        Long userEntityId = 1L;
        Long userEntitySenderId = 2L;
        String choice = "decline";

        mockMvc.perform(put("/api/users/{userEntityId}/accept-friend-request/{userEntitySenderId}", userEntityId, userEntitySenderId)
                        .param("choice", choice))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request declined"));

        verify(userEntityService, times(1)).deleteFriendRequest(userEntityId, userEntitySenderId);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testGetAllFriendRequests() throws Exception {
        Long userEntityId = 1L;
        List<FriendRequestResponseDTO> friendRequests = new ArrayList<>();

        when(userEntityService.getAllFriendRequestByUserEntityId(userEntityId)).thenReturn(friendRequests);

        mockMvc.perform(get("/api/users/{userEntityId}/friend-requests", userEntityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(friendRequests.size())));

        verify(userEntityService, times(1)).getAllFriendRequestByUserEntityId(userEntityId);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testDeleteFriendRequest() throws Exception {
        Long userEntityId = 1L;
        Long userEntityReceiverId = 2L;

        mockMvc.perform(delete("/api/users/{userEntityId}/delete-friend-request/{userEntityReceiverId}", userEntityId, userEntityReceiverId))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend deleted"));

        verify(userEntityService, times(1)).deleteFriendRequest(userEntityId, userEntityReceiverId);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testUnsubscribeRequest() throws Exception {
        Long userEntityId = 1L;
        Long userEntityReceiverId = 2L;

        mockMvc.perform(delete("/api/users/{userEntityId}/unsubscribe-request/{userEntityReceiverId}", userEntityId, userEntityReceiverId))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully unsubscribed"));

        verify(userEntityService, times(1)).sendUnsubscribeAndRecallFriendRequest(userEntityId, userEntityReceiverId);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testGetAllFriends() throws Exception {
        Long userEntityId = 1L;
        List<UserEntityResponseDTO> friendsList = new ArrayList<>();
        // ... add friend objects to the list

        when(userEntityService.getAllFriends(userEntityId)).thenReturn(friendsList);

        mockMvc.perform(get("/api/users/{userEntityId}/friends", userEntityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(friendsList.size())));

        verify(userEntityService, times(1)).getAllFriends(userEntityId);
    }
}
