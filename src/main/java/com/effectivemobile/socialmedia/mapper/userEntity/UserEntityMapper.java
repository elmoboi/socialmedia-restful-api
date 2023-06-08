package com.effectivemobile.socialmedia.mapper.userEntity;

import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRequestDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {


    UserEntityResponseDTO toResponseDTO(UserEntity userEntity);

    UserEntity toUser(UserEntityRequestDTO userEntityRequestDTO);
}
