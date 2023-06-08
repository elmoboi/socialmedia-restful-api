package com.effectivemobile.socialmedia.mapper.friend.impl;

import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestResponseDTO;
import com.effectivemobile.socialmedia.mapper.friend.FriendMapper;
import com.effectivemobile.socialmedia.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class FriendMapperImpl implements FriendMapper {

    @Override
    public FriendRequestResponseDTO toRequestResponseDTO(UserEntity userEntity) {
        FriendRequestResponseDTO friendRequestResponseDTO = new FriendRequestResponseDTO();
        friendRequestResponseDTO.setSenderId(userEntity.getId());
        friendRequestResponseDTO.setSenderUserName(userEntity.getUserName());
        friendRequestResponseDTO.setEmail(userEntity.getEmail());

        return friendRequestResponseDTO;
    }
}
