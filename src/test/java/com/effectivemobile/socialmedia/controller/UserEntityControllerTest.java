package com.effectivemobile.socialmedia.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRequestDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class UserEntityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserEntityService userEntityService;

    @MockBean
    private UserEntityMapper userEntityMapper;

    @Test
    @WithAnonymousUser
    void testGetUserEntityById_whenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userEntityId}", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testUpdateUserEntityById_whenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{userEntityId}", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testGetUserEntityById() throws Exception {
        Long userEntityId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userEntityId);
        UserEntityResponseDTO expectedResponseDTO = new UserEntityResponseDTO();
        expectedResponseDTO.setId(userEntityId);

        when(userEntityService.getUserEntityById(userEntityId)).thenReturn(userEntity);
        when(userEntityMapper.toResponseDTO(userEntity)).thenReturn(expectedResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userEntityId}", userEntityId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(userEntityId.intValue())))
                .andExpect(MockMvcResultMatchers.content().json(toJson(expectedResponseDTO)));

        verify(userEntityService, times(1)).getUserEntityById(userEntityId);
        verify(userEntityMapper, times(1)).toResponseDTO(userEntity);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"}, claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testUpdateUserEntity() throws Exception {
        UserEntity current = new UserEntity();
        current.setId(1L);
        current.setUserName("Misha");
        current.setEmail("misha@gmail.com");
        current.setPassword("testpass");

        UserEntityRequestDTO userEntityRequestDTOToUpdate = new UserEntityRequestDTO();
        userEntityRequestDTOToUpdate.setUserName("Oleg");
        userEntityRequestDTOToUpdate.setEmail("oleg@gmail.com");
        userEntityRequestDTOToUpdate.setPassword("testpass");

        UserEntity userEntityToUpdate = new UserEntity();
        userEntityToUpdate.setUserName("Oleg");
        userEntityToUpdate.setEmail("oleg@gmail.com");
        userEntityToUpdate.setPassword("testpass");

        UserEntity updatedUserEntity = new UserEntity();
        updatedUserEntity.setUserName("Oleg");
        updatedUserEntity.setEmail("oleg@gmail.com");
        updatedUserEntity.setPassword("testpass");

        UserEntityResponseDTO expectedUserEntityResponseDTO = new UserEntityResponseDTO();
        expectedUserEntityResponseDTO.setId(1L);
        expectedUserEntityResponseDTO.setUserName("Oleg");
        expectedUserEntityResponseDTO.setEmail("oleg@gmail.com");

        when(userEntityMapper.toUser(userEntityRequestDTOToUpdate)).thenReturn(userEntityToUpdate);
        when(userEntityService.updateUserEntity(current.getId(), userEntityToUpdate)).thenReturn(updatedUserEntity);
        when(userEntityMapper.toResponseDTO(updatedUserEntity)).thenReturn(expectedUserEntityResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{userEntityId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"email\": \"oleg@gmail.com\",\n" +
                                "    \"userName\": \"Oleg\",\n" +
                                "    \"password\": \"testpass\"\n" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userEntityMapper, times(1)).toUser(userEntityRequestDTOToUpdate);
        verify(userEntityService, times(1)).updateUserEntity(1L, userEntityToUpdate);
        verify(userEntityMapper, times(1)).toResponseDTO(updatedUserEntity);
    }

    private String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
}
