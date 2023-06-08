package com.effectivemobile.socialmedia.controllers;

import com.effectivemobile.socialmedia.dto.message.MessageRequestDTO;
import com.effectivemobile.socialmedia.dto.message.MessageResponseDTO;
import com.effectivemobile.socialmedia.mapper.message.MessageMapper;
import com.effectivemobile.socialmedia.model.Message;
import com.effectivemobile.socialmedia.service.message.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public MessageController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @PostMapping("/users/{userEntityId}/messages")
    public ResponseEntity<MessageResponseDTO> sendMessageToUserEntity(@PathVariable Long userEntityId,
                                                                      @Valid @RequestBody MessageRequestDTO messageRequestDTO) {
        MessageResponseDTO messageResponseDTO = messageService.sendMessage(userEntityId, messageRequestDTO);

        return ResponseEntity.ok(messageResponseDTO);
    }

    @GetMapping("/users/{userEntityId}/messages")
    public ResponseEntity<List<MessageResponseDTO>> getConversation(@PathVariable Long userEntityId,
                                                                    @RequestParam(required = false) Long recipientId) {
        List<Message> messageList = messageService.findAllMessagesBetweenUsersEntity(userEntityId, recipientId);
        List<MessageResponseDTO> messageResponseDTOList = messageMapper.toListMessageResponseDTO(messageList);

        return ResponseEntity.ok(messageResponseDTOList);
    }

}
