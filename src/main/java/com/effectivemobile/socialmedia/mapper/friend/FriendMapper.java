package com.effectivemobile.socialmedia.mapper.friend;

import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestResponseDTO;
import com.effectivemobile.socialmedia.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    FriendRequestResponseDTO toRequestResponseDTO(UserEntity userEntity);
}
