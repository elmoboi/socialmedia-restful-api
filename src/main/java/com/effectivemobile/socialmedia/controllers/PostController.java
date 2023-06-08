package com.effectivemobile.socialmedia.controllers;

import com.effectivemobile.socialmedia.dto.post.PostRequestDTO;
import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.dto.post.PostUpdateRequest;
import com.effectivemobile.socialmedia.exeption.UnauthException;
import com.effectivemobile.socialmedia.mapper.post.PostMapper;
import com.effectivemobile.socialmedia.model.Post;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.service.post.PostService;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserEntityService userEntityService;
    private final PostMapper postMapper;

    public PostController(PostService postService, UserEntityService userEntityService, PostMapper postMapper) {
        this.postService = postService;
        this.userEntityService = userEntityService;
        this.postMapper = postMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO postRequestDTO) {
        String email = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthException("You are not authorized to create this post");
        }
        UserEntity userEntity = userEntityService.findByEmail(email);
        Post post = postService.createPost(postRequestDTO, userEntity.getId());
        PostResponseDTO postResponseDTO = postMapper.toResponseDTO(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDTO);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        PostResponseDTO postResponseDTO = postMapper.toResponseDTO(post);

        return ResponseEntity.ok(postResponseDTO);
    }

    @PutMapping("/update-post/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long postId,
                                                      @Valid @RequestBody PostUpdateRequest postUpdateRequest) {
        Post updatedPost = postService.updatePost(postId, postUpdateRequest);
        PostResponseDTO postResponseDTO = postMapper.toResponseDTO(updatedPost);

        return ResponseEntity.ok(postResponseDTO);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-posts/{postOwnerId}")
    public ResponseEntity<List<PostResponseDTO>> getUserEntityPosts(@PathVariable Long postOwnerId) {

        return ResponseEntity.ok(postService.getAllOwnerPostsByOwnerId(postOwnerId));
    }
}
