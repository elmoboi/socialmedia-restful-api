package com.effectivemobile.socialmedia.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.effectivemobile.socialmedia.dto.message.MessageRequestDTO;
import com.effectivemobile.socialmedia.dto.message.MessageResponseDTO;
import com.effectivemobile.socialmedia.mapper.message.MessageMapper;
import com.effectivemobile.socialmedia.model.Message;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.service.message.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private MessageMapper messageMapper;

    @Test
    @WithAnonymousUser
    void testSendMessageToUserEntity_whenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/users/{userEntityId}/messages", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testGetConversation_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/{userEntityId}/messages", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testSendMessageToUserEntity() throws Exception {
        Long userEntityId = 1L;
        MessageRequestDTO messageRequestDTO = new MessageRequestDTO();
        messageRequestDTO.setText("Privet!");
        messageRequestDTO.setRecipientId(2L);

        MessageResponseDTO expectedResponseDTO = new MessageResponseDTO();
        expectedResponseDTO.setRecipientId(2L);
        expectedResponseDTO.setSenderId(userEntityId);
        expectedResponseDTO.setText("Privet!");

        when(messageService.sendMessage(userEntityId, messageRequestDTO)).thenReturn(expectedResponseDTO);

        mockMvc.perform(post("/api/users/{userEntityId}/messages", userEntityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(messageRequestDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.text").value(expectedResponseDTO.getText()))
                .andExpect(jsonPath("$.text").value(expectedResponseDTO.getText()))
                .andExpect(jsonPath("$.senderId").value(userEntityId))
                .andExpect(jsonPath("$.recipientId").value(2L));

        verify(messageService, times(1)).sendMessage(userEntityId, messageRequestDTO);
        verifyNoMoreInteractions(messageService);
        verifyNoInteractions(messageMapper);
    }

    @Test
    @WithMockJwtAuth(authorities = {"user", "ROLE_USER"},
            claims = @OpenIdClaims(preferredUsername = "oleg"))
    public void testGetConversation() throws Exception {
        Long userEntityId = 1L;
        Long recipientId = 2L;

        UserEntity sender = new UserEntity();
        sender.setId(1L);
        sender.setId(userEntityId);
        UserEntity recipient = new UserEntity();
        recipient.setId(2L);
        recipient.setId(recipientId);

        Message senderMessage = new Message();
        senderMessage.setSender(sender);
        senderMessage.setText("Send");

        Message recipientMessage = new Message();
        recipientMessage.setSender(recipient);
        recipientMessage.setText("Got");

        List<Message> messageList = new ArrayList<>();
        messageList.add(senderMessage);
        messageList.add(recipientMessage);

        MessageResponseDTO senderMessageResponseDTO = new MessageResponseDTO();
        senderMessageResponseDTO.setText("Send");
        senderMessageResponseDTO.setSenderId(sender.getId());
        senderMessageResponseDTO.setRecipientId(recipient.getId());

        MessageResponseDTO recipientMessageResponseDTO = new MessageResponseDTO();
        recipientMessageResponseDTO.setText("Got");
        recipientMessageResponseDTO.setSenderId(recipient.getId());
        recipientMessageResponseDTO.setRecipientId(sender.getId());

        List<MessageResponseDTO> expectedResponseDTOList = new ArrayList<>();
        expectedResponseDTOList.add(senderMessageResponseDTO);
        expectedResponseDTOList.add(recipientMessageResponseDTO);

        // Настройка mock объектов и сервисов
        when(messageService.findAllMessagesBetweenUsersEntity(userEntityId, recipientId)).thenReturn(messageList);
        when(messageMapper.toListMessageResponseDTO(messageList)).thenReturn(expectedResponseDTOList);

        // Выполнение GET запроса
        mockMvc.perform(get("/api/users/{userEntityId}/messages", userEntityId)
                        .param("recipientId", recipientId.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].text").value(expectedResponseDTOList.get(0).getText()))
                .andExpect(jsonPath("$[1].text").value(expectedResponseDTOList.get(1).getText()));

        // Проверка вызовов сервисов и маппера
        verify(messageService, times(1)).findAllMessagesBetweenUsersEntity(userEntityId, recipientId);
        verify(messageMapper, times(1)).toListMessageResponseDTO(messageList);
        verifyNoMoreInteractions(messageService);
        verifyNoMoreInteractions(messageMapper);
    }
}
