package com.amani.amaniapirest.controllers.chat;

import com.amani.amaniapirest.dto.chat.ChatConversationDTO;
import com.amani.amaniapirest.dto.chat.ChatMessageDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.services.chat.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void sendMessageReturnsCreated() throws Exception {
        ChatMessageDTO response = ChatMessageDTO.builder()
                .idMensaje(10L)
                .idSender(1L)
                .nombreSender("Laura García")
                .idReceiver(2L)
                .nombreReceiver("Carlos López")
                .mensaje("Hola Carlos")
                .enviadoEn(LocalDateTime.now())
                .leido(false)
                .build();

        when(chatService.sendMessage(any(MensajeRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/chats/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idSender":1,
                                  "idReceiver":2,
                                  "mensaje":"Hola Carlos"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMensaje").value(10))
                .andExpect(jsonPath("$.mensaje").value("Hola Carlos"));
    }

    @Test
    void sendMessageToChatReturnsCreated() throws Exception {
        ChatMessageDTO response = ChatMessageDTO.builder()
                .idMensaje(11L)
                .idSender(1L)
                .nombreSender("Laura García")
                .idReceiver(2L)
                .nombreReceiver("Carlos López")
                .mensaje("Hola")
                .enviadoEn(LocalDateTime.now())
                .leido(false)
                .build();

        when(chatService.sendMessage(any(MensajeRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/chats/1_2/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idSender":1,
                                  "idReceiver":2,
                                  "mensaje":"Hola"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Hola"));
    }

    @Test
    void sendMessageToChatReturnsBadRequestOnInvalidChatId() throws Exception {
        mockMvc.perform(post("/api/chats/invalid/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idSender":1,
                                  "idReceiver":2,
                                  "mensaje":"Hola"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChatMessagesReturnsOk() throws Exception {
        ChatMessageDTO msg = ChatMessageDTO.builder()
                .idMensaje(1L)
                .idSender(1L)
                .nombreSender("Laura García")
                .idReceiver(2L)
                .nombreReceiver("Carlos López")
                .mensaje("Hola")
                .enviadoEn(LocalDateTime.now())
                .leido(false)
                .build();

        when(chatService.getConversationMessages("1_2", 50)).thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chats/1_2/messages?limit=50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mensaje").value("Hola"));
    }

    @Test
    void getUserConversationsByUserIdReturnsOk() throws Exception {
        ChatConversationDTO conv = ChatConversationDTO.builder()
                .chatId("1_2")
                .otherUserId(2L)
                .otherUserName("Carlos López")
                .lastMessage("Hola")
                .lastMessageAt(LocalDateTime.now())
                .lastMessageRead(true)
                .unreadCount(0)
                .build();

        when(chatService.getUserConversations(1L)).thenReturn(List.of(conv));

        mockMvc.perform(get("/api/chats/conversations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatId").value("1_2"))
                .andExpect(jsonPath("$[0].otherUserName").value("Carlos López"));
    }
}
