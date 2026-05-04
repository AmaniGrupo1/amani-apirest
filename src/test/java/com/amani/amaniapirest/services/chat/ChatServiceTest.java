package com.amani.amaniapirest.services.chat;

import com.amani.amaniapirest.dto.chat.ChatConversationDTO;
import com.amani.amaniapirest.dto.chat.ChatMessageDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.events.MensajeNuevoEvent;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.MensajeRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ChatService chatService;

    private Usuario sender;
    private Usuario receiver;
    private Mensaje mensaje;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(mensajeRepository, usuarioRepository, eventPublisher);

        sender = new Usuario();
        sender.setIdUsuario(1L);
        sender.setNombre("Laura");
        sender.setApellido("García");
        sender.setEmail("laura@test.com");

        receiver = new Usuario();
        receiver.setIdUsuario(2L);
        receiver.setNombre("Carlos");
        receiver.setApellido("López");
        receiver.setEmail("carlos@test.com");

        mensaje = new Mensaje();
        mensaje.setIdMensaje(10L);
        mensaje.setSender(sender);
        mensaje.setReceiver(receiver);
        mensaje.setMensaje("Hola Carlos");
        mensaje.setEnviadoEn(LocalDateTime.now());
        mensaje.setLeido(false);
    }

    @Test
    void sendMessagePersistsAndPublishesEvent() {
        MensajeRequestDTO request = new MensajeRequestDTO();
        request.setIdSender(1L);
        request.setIdReceiver(2L);
        request.setMensaje("Hola Carlos");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);

        ChatMessageDTO result = chatService.sendMessage(request);

        assertNotNull(result);
        assertEquals(10L, result.getIdMensaje());
        assertEquals("Hola Carlos", result.getMensaje());
        verify(mensajeRepository).save(any(Mensaje.class));

        // Verify that the event was published (single RTDB write path)
        ArgumentCaptor<MensajeNuevoEvent> eventCaptor = ArgumentCaptor.forClass(MensajeNuevoEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(10L, eventCaptor.getValue().getMensaje().getIdMensaje());
    }

    @Test
    void sendMessageDoesNotCallFirebaseDirectly() {
        MensajeRequestDTO request = new MensajeRequestDTO();
        request.setIdSender(1L);
        request.setIdReceiver(2L);
        request.setMensaje("Hola Carlos");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);

        chatService.sendMessage(request);

        // Only event published — no direct Firebase dependency in ChatService
        verify(eventPublisher).publishEvent(any(MensajeNuevoEvent.class));
        // ChatService no longer has FirebaseChatService dependency
        verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    void sendMessageThrowsWhenSenderNotFound() {
        MensajeRequestDTO request = new MensajeRequestDTO();
        request.setIdSender(99L);
        request.setIdReceiver(2L);
        request.setMensaje("Hola");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(request));
        verify(mensajeRepository, never()).save(any());
    }

    @Test
    void sendMessageThrowsWhenReceiverNotFound() {
        MensajeRequestDTO request = new MensajeRequestDTO();
        request.setIdSender(1L);
        request.setIdReceiver(99L);
        request.setMensaje("Hola");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(request));
    }

    @Test
    void getConversationMessagesReturnsMessagesInChronologicalOrder() {
        Mensaje m1 = new Mensaje();
        m1.setIdMensaje(1L);
        m1.setSender(sender);
        m1.setReceiver(receiver);
        m1.setMensaje("Hola");
        m1.setEnviadoEn(LocalDateTime.now().minusMinutes(10));
        m1.setLeido(true);

        Mensaje m2 = new Mensaje();
        m2.setIdMensaje(2L);
        m2.setSender(receiver);
        m2.setReceiver(sender);
        m2.setMensaje("Hey!");
        m2.setEnviadoEn(LocalDateTime.now());
        m2.setLeido(false);

        when(mensajeRepository.findRecentConversationMessages(1L, 2L))
                .thenReturn(List.of(m2, m1));

        List<ChatMessageDTO> result = chatService.getConversationMessages("1_2", 50);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getIdMensaje());
        assertEquals(2L, result.get(1).getIdMensaje());
    }

    @Test
    void getConversationMessagesThrowsOnInvalidChatId() {
        assertThrows(IllegalArgumentException.class, () -> chatService.getConversationMessages("invalid", 50));
        assertThrows(IllegalArgumentException.class, () -> chatService.getConversationMessages("1_2_3", 50));
    }

    @Test
    void canAccessChatReturnsTrueForParticipant() {
        assertTrue(chatService.canAccessChat("1_2", 1L));
        assertTrue(chatService.canAccessChat("1_2", 2L));
    }

    @Test
    void canAccessChatReturnsFalseForNonParticipant() {
        assertFalse(chatService.canAccessChat("1_2", 99L));
    }

    @Test
    void canAccessChatReturnsFalseForInvalidChatId() {
        assertFalse(chatService.canAccessChat(null, 1L));
        assertFalse(chatService.canAccessChat("invalid", 1L));
        assertFalse(chatService.canAccessChat("1_2_3", 1L));
    }

    @Test
    void getConversationIdGeneratesStableId() {
        String id1 = chatService.getConversationId(1L, 2L);
        String id2 = chatService.getConversationId(2L, 1L);
        assertEquals("1_2", id1);
        assertEquals("1_2", id2);
    }

    @Test
    void getUserConversationsReturnsOrderedConversations() {
        Mensaje m1 = new Mensaje();
        m1.setIdMensaje(1L);
        m1.setSender(sender);
        m1.setReceiver(receiver);
        m1.setMensaje("Mensaje 1");
        m1.setEnviadoEn(LocalDateTime.now().minusHours(1));
        m1.setLeido(true);

        when(mensajeRepository.findBySender_IdUsuario(1L)).thenReturn(List.of(m1));
        when(mensajeRepository.findByReceiver_IdUsuario(1L)).thenReturn(List.of());

        List<ChatConversationDTO> conversations = chatService.getUserConversations(1L);

        assertEquals(1, conversations.size());
        assertEquals("1_2", conversations.get(0).getChatId());
        assertEquals(2L, conversations.get(0).getOtherUserId());
    }
}
