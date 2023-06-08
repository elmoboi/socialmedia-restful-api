package com.effectivemobile.socialmedia.service.message.impl;

import com.effectivemobile.socialmedia.dto.message.MessageRequestDTO;
import com.effectivemobile.socialmedia.dto.message.MessageResponseDTO;
import com.effectivemobile.socialmedia.exeption.UnauthException;
import com.effectivemobile.socialmedia.mapper.message.MessageMapper;
import com.effectivemobile.socialmedia.model.Message;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.repository.MessageRepository;
import com.effectivemobile.socialmedia.repository.UserEntityRepository;
import com.effectivemobile.socialmedia.service.message.MessageService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserEntityRepository userEntityRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository, UserEntityRepository userEntityRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.userEntityRepository = userEntityRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public void delete(Message message) {
        messageRepository.delete(message);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public MessageResponseDTO sendMessage(Long userEntityId, MessageRequestDTO messageRequestDTO) {
        UserEntity userEntitySender = userEntityRepository.findById(userEntityId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userEntityId));
        UserEntity userEntityRecipient = userEntityRepository.findById(messageRequestDTO.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userEntityId));

        Message message = new Message();
        message.setSender(userEntitySender);
        message.setRecipient(userEntityRecipient);
        message.setText(messageRequestDTO.getText());
        message.setSendDate(LocalDateTime.now());

        assert userEntitySender.getFriendsList() != null;
        if (!userEntitySender.getFriendsList().contains(userEntityRecipient)) {
            throw new UnauthException("You are not in friendship to send message");
        } else {
            Message saveMessage = messageRepository.save(message);
            return messageMapper.toMessageResponseDTO(saveMessage);
        }
    }

    @Override
    public List<Message> findAllMessagesBetweenUsersEntity(Long senderId, Long recipientId) {
        List<Message> messageList = messageRepository.findAllBySenderIdAndRecipientId(senderId, recipientId);
        messageList.addAll(messageRepository.findAllBySenderIdAndRecipientId(recipientId, senderId));
        messageList.sort(Comparator.comparing(Message::getSendDate));

        return messageList;
    }
}
