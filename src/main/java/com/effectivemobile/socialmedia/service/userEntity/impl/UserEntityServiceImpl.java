package com.effectivemobile.socialmedia.service.userEntity.impl;

import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestDTO;
import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestResponseDTO;
import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRegistrationRequest;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.mapper.friend.FriendMapper;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.Role;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.repository.RoleRepository;
import com.effectivemobile.socialmedia.repository.UserEntityRepository;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserEntityMapper userEntityMapper;
    private final FriendMapper friendMapper;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder,
                                 RoleRepository roleRepository, UserEntityMapper userEntityMapper, FriendMapper friendMapper) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userEntityMapper = userEntityMapper;
        this.friendMapper = friendMapper;
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }

    @Override
    public void delete(UserEntity userEntity) {
        userEntityRepository.delete(userEntity);
    }

    @Override
    public List<UserEntity> findAll() {
        return userEntityRepository.findAll();
    }

    @Override
    public UserEntity getUserEntityById(Long id) {
        return userEntityRepository.findById(id).orElseThrow(() ->
                new OpenApiResourceNotFoundException("User with id: " + id + " not found"));
    }

    @Override
    public UserEntity findFirstByEmail(String email) {
        return userEntityRepository.findFirstByEmail(email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Override
    public UserEntity findUserEntityByUserEntityName(String name) {
        return userEntityRepository.findUserEntitiesByUserName(name);
    }

    @Override
    public UserEntity registerUserEntity(UserEntityRegistrationRequest registrationRequest) {
        if (userEntityRepository.existsByEmail(registrationRequest.getEmail()))
            throw new IllegalArgumentException("Email is already taken");

        if (userEntityRepository.existsByUserName(registrationRequest.getUserName()))
            throw new IllegalArgumentException("User name is already taken");

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setUserName(registrationRequest.getUserName());
        newUserEntity.setEmail(registrationRequest.getEmail());
        newUserEntity.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                new IllegalArgumentException("role not found"));
        newUserEntity.setRoles(Collections.singleton(role));

        return userEntityRepository.save(newUserEntity);
    }

    @Override
    public UserEntity updateUserEntity(Long id, UserEntity userEntityToUpdate) {
        UserEntity updatedUserEntity = userEntityRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + id + " not found"));

        updatedUserEntity.setUserName(userEntityToUpdate.getUserName());
        updatedUserEntity.setLastName(userEntityToUpdate.getLastName());
        updatedUserEntity.setEmail(userEntityToUpdate.getEmail());

        return userEntityRepository.save(updatedUserEntity);
    }

    @Override
    public void sendFriendRequest(Long id, FriendRequestDTO friendRequestDTO) {
        UserEntity userEntitySender = getUserEntityById(friendRequestDTO.getSenderId());
        UserEntity userEntityReceiver = getUserEntityById(friendRequestDTO.getReceiverId());

        assert userEntitySender.getFriendsList() != null;
        assert userEntityReceiver.getFriendsList() != null;
        assert userEntitySender.getSubscriberList() != null;
        assert userEntityReceiver.getSubscriberList() != null;
        assert userEntitySender.getSingerList() != null;

        if (userEntitySender.getFriendsList().contains(userEntityReceiver))
            throw new IllegalArgumentException("There is already friendship between those users");
        if (userEntityReceiver.getReceiverFriendRequests().contains(userEntitySender))
            throw new IllegalArgumentException("There is already friend request to this user");

        if (userEntitySender.getSubscriberList().contains(userEntityReceiver)) {
            userEntitySender.getFriendsList().add(userEntityReceiver);
            userEntityReceiver.getFriendsList().add(userEntitySender);

            userEntityReceiver.getSubscriberList().add(userEntitySender);
            userEntitySender.getSingerList().add(userEntityReceiver);

            userEntityReceiver.getReceiverFriendRequests().remove(userEntitySender);
            userEntitySender.getReceiverFriendRequests().remove(userEntityReceiver);
        } else {
            userEntityReceiver.getSubscriberList().add(userEntitySender);
            userEntitySender.getSingerList().add(userEntityReceiver);
            userEntityReceiver.getReceiverFriendRequests().add(userEntitySender);
        }

        userEntityRepository.save(userEntityReceiver);
    }

    @Override
    public List<FriendRequestResponseDTO> getAllFriendRequestByUserEntityId(Long id) {
        UserEntity userEntity = userEntityRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + id + " not found"));

        List<UserEntity> receivedFriendRequests = userEntity.getReceiverFriendRequests();

        return receivedFriendRequests.stream()
                .map(friendMapper::toRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void acceptFriendRequest(Long userEntityId, Long userEntitySenderId) {
        UserEntity userEntity = userEntityRepository.findById(userEntityId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntityId + " not found"));
        UserEntity userEntitySender = userEntityRepository.findById(userEntitySenderId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntitySenderId + " not found"));

        assert userEntity.getSubscriberList() != null;
        assert userEntity.getFriendsList() != null;
        assert userEntitySender.getFriendsList() != null;
        assert userEntitySender.getSubscriberList() != null;

        if (!userEntity.getSubscriberList().contains(userEntitySender))
            throw new IllegalArgumentException("Invalid friend request");

        if (userEntity.getFriendsList().contains(userEntitySender))
            throw new IllegalArgumentException("Request already provided");

        userEntity.getFriendsList().add(userEntitySender);
        userEntitySender.getFriendsList().add(userEntity);
        userEntitySender.getSubscriberList().add(userEntity);
        userEntity.getReceiverFriendRequests().remove(userEntitySender);

        userEntityRepository.save(userEntity);
        userEntityRepository.save(userEntitySender);
    }

    @Override
    public List<UserEntityResponseDTO> getAllFriends(Long userEntityId) {
        UserEntity userEntity = userEntityRepository.findById(userEntityId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntityId + "not found"));

        assert userEntity.getFriendsList() != null;

        return userEntity.getFriendsList().stream().map(userEntityMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserEntityResponseDTO> getUserEntitySignerList(Long userEntityId) {
        UserEntity userEntity = userEntityRepository.findById(userEntityId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntityId + "not found"));

        assert userEntity.getSingerList() != null;

        return userEntity.getSingerList().stream().map(userEntityMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteFriendRequest(Long userEntitySenderId, Long userEntityReceiverId) {
        UserEntity userEntitySender = userEntityRepository.findById(userEntitySenderId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntitySenderId + " not found"));
        UserEntity userEntityReceiver = userEntityRepository.findById(userEntityReceiverId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntityReceiverId + " not found"));

        assert userEntitySender.getFriendsList() != null;
        assert userEntityReceiver.getFriendsList() != null;
        assert userEntitySender.getSubscriberList() != null;
        assert userEntityReceiver.getSubscriberList() != null;

        if (!userEntitySender.getFriendsList().contains(userEntityReceiver))
            throw new IllegalArgumentException("Invalid friend delete request");

        userEntitySender.getFriendsList().remove(userEntityReceiver);
        userEntityReceiver.getFriendsList().remove(userEntitySender);
        userEntityReceiver.getSubscriberList().remove(userEntitySender);

        userEntityRepository.save(userEntitySender);
        userEntityRepository.save(userEntityReceiver);
    }

    @Override
    public void sendUnsubscribeAndRecallFriendRequest(Long userEntitySenderId, Long userEntityReceiverId) {
        UserEntity userEntitySender = userEntityRepository.findById(userEntitySenderId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntitySenderId + " not found"));
        UserEntity userEntityReceiver = userEntityRepository.findById(userEntityReceiverId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("User with id: " + userEntityReceiverId + " not found"));

        assert userEntityReceiver.getSubscriberList() != null;
        assert userEntitySender.getSubscriberList() != null;

        if (!userEntityReceiver.getSubscriberList().contains(userEntitySender))
            throw new IllegalArgumentException("Invalid friend delete request");

        userEntityReceiver.getSubscriberList().remove(userEntitySender);
        userEntityReceiver.getReceiverFriendRequests().remove(userEntitySender);

        userEntityRepository.save(userEntitySender);
    }

    @Override
    public List<PostResponseDTO> getUserEntityActivityFeedPosts(Long userEntityId) {
        return null;
    }
}
