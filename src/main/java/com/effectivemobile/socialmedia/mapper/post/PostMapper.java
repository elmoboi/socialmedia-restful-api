package com.effectivemobile.socialmedia.mapper.post;

import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponseDTO toResponseDTO(Post post);
}
