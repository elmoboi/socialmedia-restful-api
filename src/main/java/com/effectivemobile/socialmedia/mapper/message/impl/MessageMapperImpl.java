package com.effectivemobile.socialmedia.mapper.message.impl;

import com.effectivemobile.socialmedia.dto.message.MessageResponseDTO;
import com.effectivemobile.socialmedia.mapper.message.MessageMapper;
import com.effectivemobile.socialmedia.model.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapperImpl implements MessageMapper {
    @Override
    public List<MessageResponseDTO> toListMessageResponseDTO(List<Message> messageList) {
        return messageList.stream()
                .map(this::toMessageResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDTO toMessageResponseDTO(Message message) {
        MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
        messageResponseDTO.setId(message.getId());
        messageResponseDTO.setText(message.getText());
        messageResponseDTO.setSendDate(message.getSendDate());
        messageResponseDTO.setSenderId(message.getSender().getId());
        messageResponseDTO.setRecipientId(message.getRecipient().getId());

        return messageResponseDTO;
    }
}
