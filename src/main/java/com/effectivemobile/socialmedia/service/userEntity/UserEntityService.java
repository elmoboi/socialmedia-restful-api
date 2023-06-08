package com.effectivemobile.socialmedia.service.userEntity;

import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestDTO;
import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestResponseDTO;
import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRegistrationRequest;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.model.UserEntity;

import java.util.List;

public interface UserEntityService {

    UserEntity save(UserEntity userEntity);

    void delete(UserEntity userEntity);

    List<UserEntity> findAll();

    UserEntity getUserEntityById(Long id);

    UserEntity findFirstByEmail(String email);

    UserEntity findByEmail(String email);

    UserEntity findUserEntityByUserEntityName(String name);

    UserEntity registerUserEntity(UserEntityRegistrationRequest registrationRequest);

    UserEntity updateUserEntity(Long id, UserEntity userEntityToUpdate);

    void sendFriendRequest(Long id, FriendRequestDTO friendRequestDTO);

    List<FriendRequestResponseDTO> getAllFriendRequestByUserEntityId(Long id);

    void acceptFriendRequest(Long userEntityId, Long userEntitySenderId);

    void deleteFriendRequest(Long userEntitySenderId, Long userEntityReceiverId);

    void sendUnsubscribeAndRecallFriendRequest(Long userEntitySenderId, Long userEntityReceiverId);

    List<UserEntityResponseDTO> getAllFriends(Long userEntityId);

    List<UserEntityResponseDTO> getUserEntitySignerList(Long userEntityId);

    List<PostResponseDTO> getUserEntityActivityFeedPosts(Long userEntityId);

}
