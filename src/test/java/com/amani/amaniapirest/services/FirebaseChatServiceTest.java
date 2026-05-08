package com.amani.amaniapirest.services;

import com.amani.amaniapirest.gateway.ChatGateway;
import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseChatServiceTest {

    @Mock
    private ChatGateway chatGateway;

    @Mock
    private PushNotificationGateway pushGateway;

    @InjectMocks
    private FirebaseChatService service;

    @Test
    @DisplayName("getConversationId delega en chatGateway")
    void getConversationId_delegaEnChatGateway() {
        when(chatGateway.getConversationId(1L, 2L)).thenReturn("1_2");

        String result = service.getConversationId(1L, 2L);

        assert result.equals("1_2");
        verify(chatGateway).getConversationId(1L, 2L);
    }

    @Test
    @DisplayName("enviarMensaje con datos válidos llama a ambos gateways")
    void enviarMensaje_conDatosValidos_llamaAAmbosGateways() {
        Usuario sender = new Usuario(); sender.setIdUsuario(1L); sender.setFcmToken("tok1");
        Usuario receiver = new Usuario(); receiver.setIdUsuario(2L); receiver.setFcmToken("tok2");
        Mensaje mensaje = new Mensaje(); mensaje.setSender(sender); mensaje.setReceiver(receiver);
        mensaje.setMensaje("Hola"); mensaje.setLeido(false);

        when(chatGateway.getConversationId(1L, 2L)).thenReturn("1_2");

        service.enviarMensaje(mensaje);

        verify(chatGateway).sendMessage(any(), any(), any(), any(), any(), any(), anyBoolean(), any());
        verify(pushGateway).sendPush("tok2", "Nuevo mensaje", "Hola");
    }

    @Test
    @DisplayName("enviarMensaje sin sender no llama a gateways")
    void enviarMensaje_sinSender_noLlamaAGateways() {
        Mensaje mensaje = new Mensaje(); mensaje.setSender(null); mensaje.setReceiver(new Usuario());

        service.enviarMensaje(mensaje);

        verify(chatGateway, never()).sendMessage(any(), any(), any(), any(), any(), any(), anyBoolean(), any());
        verify(pushGateway, never()).sendPush(any(), any(), any());
    }

    @Test
    @DisplayName("enviarMensaje sin receiver no llama a gateways")
    void enviarMensaje_sinReceiver_noLlamaAGateways() {
        Mensaje mensaje = new Mensaje(); mensaje.setSender(new Usuario()); mensaje.setReceiver(null);

        service.enviarMensaje(mensaje);

        verify(chatGateway, never()).sendMessage(any(), any(), any(), any(), any(), any(), anyBoolean(), any());
        verify(pushGateway, never()).sendPush(any(), any(), any());
    }

    @Test
    @DisplayName("userCanAccessChat delega en chatGateway")
    void userCanAccessChat_delegaEnChatGateway() {
        when(chatGateway.userCanAccessChat("1_2", 1L)).thenReturn(true);

        boolean result = service.userCanAccessChat("1_2", 1L);

        assert result;
        verify(chatGateway).userCanAccessChat("1_2", 1L);
    }
}
