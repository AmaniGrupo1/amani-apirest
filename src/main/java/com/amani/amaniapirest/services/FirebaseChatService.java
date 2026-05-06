package com.amani.amaniapirest.services;

import com.amani.amaniapirest.gateway.ChatGateway;
import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.amani.amaniapirest.models.Mensaje;
import com.google.firebase.FirebaseApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio para chat que delega en los gateways.
 *
 * <p>Explica la lógica de negocio del chat y delega la infraestructura
 * (RTDB, push) en las abstracciones {@link ChatGateway} y
 * {@link PushNotificationGateway}.</p>
 *
 * <p>Solo se instancia si {@link FirebaseApp} está disponible.</p>
 */
@Service
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseChatService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseChatService.class);

    private final ChatGateway chatGateway;
    private final PushNotificationGateway pushGateway;

    public FirebaseChatService(ChatGateway chatGateway,
                                PushNotificationGateway pushGateway) {
        this.chatGateway = chatGateway;
        this.pushGateway = pushGateway;
    }

    /**
     * Genera un identificador estable para la sala de chat entre dos usuarios.
     */
    public String getConversationId(Long user1Id, Long user2Id) {
        return chatGateway.getConversationId(user1Id, user2Id);
    }

    /**
     * Envía un mensaje al gateway de chat y notificación push.
     */
    @Async
    public void enviarMensaje(Mensaje mensaje) {
        Long senderId = mensaje.getSender() != null ? mensaje.getSender().getIdUsuario() : null;
        Long receiverId = mensaje.getReceiver() != null ? mensaje.getReceiver().getIdUsuario() : null;

        if (senderId == null || receiverId == null) {
            log.warn("[Chat] Mensaje sin remitente o receptor, se omite. idSender={} idReceiver={}", senderId, receiverId);
            return;
        }

        String chatId = chatGateway.getConversationId(senderId, receiverId);

        chatGateway.sendMessage(
                chatId,
                senderId,
                receiverId,
                mensaje.getMensaje(),
                mensaje.getEnviadoEn() != null ? mensaje.getEnviadoEn().toString() : null,
                mensaje.getIdMensaje(),
                mensaje.isLeido(),
                mensaje.getCita() != null ? mensaje.getCita().getIdCita() : null
        );

        if (mensaje.getReceiver() != null && mensaje.getReceiver().getFcmToken() != null) {
            pushGateway.sendPush(
                    mensaje.getReceiver().getFcmToken(),
                    "Nuevo mensaje",
                    mensaje.getMensaje());
        }
    }

    /**
     * Verifica que un usuario pertenezca a la conversación indicada.
     */
    public boolean userCanAccessChat(String chatId, Long userId) {
        return chatGateway.userCanAccessChat(chatId, userId);
    }
}
