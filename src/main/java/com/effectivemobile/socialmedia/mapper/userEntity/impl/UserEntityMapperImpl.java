package com.effectivemobile.socialmedia.mapper.userEntity.impl;

import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRequestDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapperImpl implements UserEntityMapper {

    @Override
    public UserEntityResponseDTO toResponseDTO(UserEntity userEntity) {
        if (userEntity == null)
            return null;

        UserEntityResponseDTO userEntityResponseDTO = new UserEntityResponseDTO();
        userEntityResponseDTO.setId(userEntity.getId());
        userEntityResponseDTO.setEmail(userEntity.getEmail());
        userEntityResponseDTO.setUserName(userEntity.getUserName());
        userEntityResponseDTO.setLastName(userEntity.getLastName());

        return userEntityResponseDTO;
    }

    @Override
    public UserEntity toUser(UserEntityRequestDTO userEntityRequestDTO) {
        if (userEntityRequestDTO == null)
            return null;

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userEntityRequestDTO.getUserName());
        userEntity.setLastName(userEntityRequestDTO.getLastName());
        userEntity.setEmail(userEntityRequestDTO.getEmail());

        return userEntity;
    }
}
