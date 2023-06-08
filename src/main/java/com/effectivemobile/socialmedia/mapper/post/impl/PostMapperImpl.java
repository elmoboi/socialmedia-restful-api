package com.effectivemobile.socialmedia.mapper.post.impl;

import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.mapper.post.PostMapper;
import com.effectivemobile.socialmedia.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapperImpl implements PostMapper {
    @Override
    public PostResponseDTO toResponseDTO(Post post) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        postResponseDTO.setId(post.getId());
        postResponseDTO.setTitle(post.getTitle());
        postResponseDTO.setText(post.getText());
        postResponseDTO.setImgUrl(post.getImg());
        postResponseDTO.setSendDate(post.getCreationDate());
        postResponseDTO.setModifiedDate(post.getModifiedDate());
        postResponseDTO.setSenderId(post.getPostOwner().getId());

        return postResponseDTO;
    }
}
