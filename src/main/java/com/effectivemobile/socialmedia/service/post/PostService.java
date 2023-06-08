package com.effectivemobile.socialmedia.service.post;

import com.effectivemobile.socialmedia.dto.post.PostRequestDTO;
import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.dto.post.PostUpdateRequest;
import com.effectivemobile.socialmedia.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    Post save(Post post);

    void delete(Post post);

    Post getPostById(Long postId);

    List<PostResponseDTO> getAllOwnerPostsByOwnerId(Long userEntityId);

    Page<PostResponseDTO> getPagedAndSortedUserEntityActivityFeed(Long userEntityId, Pageable pageable);

    List<Post> findAll();

    Post createPost(PostRequestDTO postRequestDTO, Long userEntityId);

    void deletePost(Long postId);

    Post updatePost(Long postId, PostUpdateRequest postUpdateRequest);


}
