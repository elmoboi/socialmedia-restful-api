package com.effectivemobile.socialmedia.mapper.message;

import com.effectivemobile.socialmedia.dto.message.MessageResponseDTO;
import com.effectivemobile.socialmedia.model.Message;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    List<MessageResponseDTO> toListMessageResponseDTO(List<Message> messageList);

    MessageResponseDTO toMessageResponseDTO(Message message);
}
