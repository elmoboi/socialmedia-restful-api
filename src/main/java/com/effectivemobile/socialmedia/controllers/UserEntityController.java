package com.effectivemobile.socialmedia.controllers;

import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRequestDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserEntityController {

    private final UserEntityService userEntityService;
    private final UserEntityMapper userEntityMapper;

    public UserEntityController(UserEntityService userEntityService, UserEntityMapper userEntityMapper) {
        this.userEntityService = userEntityService;
        this.userEntityMapper = userEntityMapper;
    }

    @GetMapping("/{userEntityId}")
    public ResponseEntity<UserEntityResponseDTO> getUserEntityById(@PathVariable Long userEntityId) {
        UserEntity userEntity = userEntityService.getUserEntityById(userEntityId);
        UserEntityResponseDTO userEntityRequestDTO = userEntityMapper.toResponseDTO(userEntity);

        return ResponseEntity.ok(userEntityRequestDTO);
    }

    @PutMapping("/{userEntityId}")
    public ResponseEntity<UserEntityResponseDTO> updateUserEntity(
            @PathVariable Long userEntityId, @Valid @RequestBody UserEntityRequestDTO userEntityRequestDTO) {
        UserEntity userEntityToUpdate = userEntityMapper.toUser(userEntityRequestDTO);
        UserEntity updatedUserEntity = userEntityService.updateUserEntity(userEntityId, userEntityToUpdate);
        UserEntityResponseDTO userEntityResponseDTO = userEntityMapper.toResponseDTO(updatedUserEntity);

        return ResponseEntity.ok(userEntityResponseDTO);
    }


}
