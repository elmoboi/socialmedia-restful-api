package com.effectivemobile.socialmedia.service.post.impl;

import com.effectivemobile.socialmedia.dto.post.PostRequestDTO;
import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.dto.post.PostUpdateRequest;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.mapper.post.PostMapper;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.Post;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.repository.PostRepository;
import com.effectivemobile.socialmedia.repository.UserEntityRepository;
import com.effectivemobile.socialmedia.service.post.PostService;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Service;

import javax.persistence.EntityListeners;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EntityListeners(AuditingEntityListener.class)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserEntityRepository userEntityRepository;
    private final PostMapper postMapper;
    private final UserEntityMapper userEntityMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserEntityRepository userEntityRepository,
                           PostMapper postMapper, UserEntityMapper userEntityMapper) {
        this.postRepository = postRepository;
        this.userEntityRepository = userEntityRepository;
        this.postMapper = postMapper;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public Post save(Post post) {

        return postRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Override
    public Post getPostById(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Post with id: " + postId + " not found"));
    }

    @Override
    public List<Post> findAll() {

        return postRepository.findAll();
    }

    @Override
    public List<PostResponseDTO> getAllOwnerPostsByOwnerId(Long postOwnerId) {
        List<Post> postList = postRepository.getPostsByPostOwner_Id(postOwnerId);

        return postList.stream()
                .map(postMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PostResponseDTO> getPagedAndSortedUserEntityActivityFeed(Long userEntityId, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findById(userEntityId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntityId + " not found"));

        assert userEntity.getSingerList() != null;
        List<UserEntityResponseDTO> userEntitySignerList = new ArrayList<>(userEntity.getSingerList()).stream()
                .map(userEntityMapper::toResponseDTO)
                .toList();

        List<PostResponseDTO> postResponseDTOList = userEntitySignerList.stream()
                .flatMap(userEntityResponseDTO ->
                        new ArrayList<>(getAllOwnerPostsByOwnerId(userEntityResponseDTO.getId())).stream())
                .collect(Collectors.toList());

        if (postResponseDTOList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postResponseDTOList.size());

        List<PostResponseDTO> pagedUserEntityActivityFeed = postResponseDTOList.subList(start, end);

        pagedUserEntityActivityFeed.sort(Comparator.comparing(
                        PostResponseDTO::getModifiedDate,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(PostResponseDTO::getId));

        return new PageImpl<>(pagedUserEntityActivityFeed, pageable, postResponseDTOList.size());
    }

    @Override
    public Post createPost(PostRequestDTO postRequestDTO, Long userEntityId) {
        Post post = new Post();
        post.setTitle(postRequestDTO.getTitle());
        post.setText(postRequestDTO.getText());
        post.setImg(postRequestDTO.getImgUrl());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userEntityId);
        post.setPostOwner(userEntity);


        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }

    @Override
    public Post updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        Post post = getPostById(postId);
        post.setTitle(postUpdateRequest.getTitle());
        post.setText(postUpdateRequest.getText());
        post.setImg(postUpdateRequest.getImgUrl());

        return postRepository.save(post);
    }
}
