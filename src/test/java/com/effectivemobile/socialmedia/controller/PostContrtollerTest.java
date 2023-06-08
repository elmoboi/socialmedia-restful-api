package com.effectivemobile.socialmedia.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.effectivemobile.socialmedia.dto.post.PostRequestDTO;
import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.dto.post.PostUpdateRequest;
import com.effectivemobile.socialmedia.mapper.post.PostMapper;
import com.effectivemobile.socialmedia.model.Post;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.service.post.PostService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class PostContrtollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserEntityService userEntityService;

    @MockBean
    private PostMapper postMapper;

    @Test
    @WithAnonymousUser
    void testPostAccess_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testCreatePost_whenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/posts/create"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testGetPostById_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testUpdatePost_whenUnauthorized() throws Exception {
        mockMvc.perform(put("/api/posts/update-post/{postId}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testDeletePost_whenUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testGetUserEntityPosts_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/posts/user-posts/{postOwnerId}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testCreatePost() throws Exception {
        PostRequestDTO postRequestDTO = new PostRequestDTO();
        postRequestDTO.setTitle("title");
        postRequestDTO.setText("text");
        postRequestDTO.setImgUrl("imgUrl");

        PostResponseDTO expectedResponseDTO = new PostResponseDTO();
        expectedResponseDTO.setTitle("title");
        expectedResponseDTO.setText("text");
        expectedResponseDTO.setImgUrl("imgUrl");
        expectedResponseDTO.setSenderId(1L);
        expectedResponseDTO.setId(1L);

        Post post = new Post();
        post.setTitle("title");
        post.setText("text");
        post.setImg("imgUrl");
        post.setId(1L);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("oleg");
        userEntity.setEmail("oleg@test.com");
        userEntity.setId(1L);

        when(userEntityService.findByEmail(anyString())).thenReturn(userEntity);
        when(postService.createPost(postRequestDTO, 1L)).thenReturn(post);
        when(postMapper.toResponseDTO(post)).thenReturn(expectedResponseDTO);

        mockMvc.perform(post("/api/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResponseDTO.getId()))
                .andExpect(jsonPath("$.title").value(expectedResponseDTO.getTitle()))
                .andExpect(jsonPath("$.text").value(expectedResponseDTO.getText()));

        verify(postService, times(1)).createPost(postRequestDTO, 1L);
        verify(postMapper, times(1)).toResponseDTO(post);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testGetPostById() throws Exception {
        Long postId = 1L;
        Post post = new Post();
        PostResponseDTO expectedResponseDTO = new PostResponseDTO();

        when(postService.getPostById(postId)).thenReturn(post);
        when(postMapper.toResponseDTO(post)).thenReturn(expectedResponseDTO);

        mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponseDTO.getId()))
                .andExpect(jsonPath("$.title").value(expectedResponseDTO.getTitle()))
                .andExpect(jsonPath("$.text").value(expectedResponseDTO.getText()));

        verify(postService, times(1)).getPostById(postId);
        verify(postMapper, times(1)).toResponseDTO(post);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testUpdatePost() throws Exception {
        Long postId = 1L;

        PostRequestDTO postCreateRequest = new PostRequestDTO();
        postCreateRequest.setTitle("title");
        postCreateRequest.setText("text");
        postCreateRequest.setImgUrl("imgUrl");

        Post createdPost = new Post();
        createdPost.setId(1L);
        createdPost.setTitle("titleUpdate");
        createdPost.setText("textUpdate");
        createdPost.setImg("imgUrl");

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest();
        postUpdateRequest.setTitle("titleUpdate");
        postUpdateRequest.setText("textUpdate");
        postUpdateRequest.setImgUrl("imgUrlUpdate");

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setTitle("titleUpdate");
        updatedPost.setText("textUpdate");
        updatedPost.setImg("imgUrlUpdate");

        PostResponseDTO expectedResponseDTO = new PostResponseDTO();
        expectedResponseDTO.setId(1L);
        expectedResponseDTO.setTitle("titleUpdate");
        expectedResponseDTO.setText("textUpdate");
        expectedResponseDTO.setImgUrl("imgUrlUpdate");

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("oleg");
        userEntity.setEmail("oleg@test.com");
        userEntity.setId(1L);

        when(userEntityService.findByEmail(anyString())).thenReturn(userEntity);

        when(postService.createPost(postCreateRequest, 1L)).thenReturn(createdPost);
        when(postService.updatePost(createdPost.getId(), postUpdateRequest)).thenReturn(updatedPost);
        when(postMapper.toResponseDTO(updatedPost)).thenReturn(expectedResponseDTO);

        mockMvc.perform(post("/api/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postCreateRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/posts/update-post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value(expectedResponseDTO.getTitle()))
                .andExpect(jsonPath("$.text").value(expectedResponseDTO.getText()));

        verify(postService, times(1)).updatePost(postId, postUpdateRequest);
        verify(postMapper, times(1)).toResponseDTO(updatedPost);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testDeletePost() throws Exception {
        Long postId = 1L;

        PostRequestDTO postCreateRequest = new PostRequestDTO();
        postCreateRequest.setTitle("title");
        postCreateRequest.setText("text");
        postCreateRequest.setImgUrl("imgUrl");

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("oleg");
        userEntity.setEmail("oleg@test.com");
        userEntity.setId(1L);

        when(userEntityService.findByEmail(anyString())).thenReturn(userEntity);

        mockMvc.perform(post("/api/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postCreateRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isOk());

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testGetUserEntityPosts() throws Exception {
        Long postOwnerId = 1L;
        List<PostResponseDTO> expectedResponseDTOList = new ArrayList<>();

        PostRequestDTO postCreateRequest = new PostRequestDTO();
        postCreateRequest.setTitle("title");
        postCreateRequest.setText("text");
        postCreateRequest.setImgUrl("imgUrl");

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("oleg");
        userEntity.setEmail("oleg@test.com");
        userEntity.setId(1L);

        when(userEntityService.findByEmail(anyString())).thenReturn(userEntity);

        mockMvc.perform(post("/api/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postCreateRequest)))
                .andExpect(status().isCreated());

        when(postService.getAllOwnerPostsByOwnerId(userEntity.getId())).thenReturn(expectedResponseDTOList);

        mockMvc.perform(get("/api/posts/user-posts/{postOwnerId}", userEntity.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResponseDTOList.size())));

        verify(postService, times(1)).getAllOwnerPostsByOwnerId(userEntity.getId());
    }
}
