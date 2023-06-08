package com.effectivemobile.socialmedia.service.message;

import com.effectivemobile.socialmedia.dto.message.MessageRequestDTO;
import com.effectivemobile.socialmedia.dto.message.MessageResponseDTO;
import com.effectivemobile.socialmedia.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message save(Message message);

    void delete(Message message);

    List<Message> findAll();

    Optional<Message> findById(Long id);

    MessageResponseDTO sendMessage(Long userEntityId, MessageRequestDTO messageRequestDTO);

    List<Message> findAllMessagesBetweenUsersEntity(Long senderId, Long recipientId);

}
