package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.MensajeNuevoEvent;
import com.amani.amaniapirest.gateway.ChatGateway;
import com.amani.amaniapirest.gateway.PushNotificationGateway;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener del dominio {@link Mensaje} adaptado para no usar WebSocket.
 *
 * <p>Al recibir un {@link MensajeNuevoEvent} escribe el mensaje en el gateway
 * de chat en tiempo real y desencadena notificación push.</p>
 *
 * <p>Depende de las abstracciones {@link ChatGateway} y
 * {@link PushNotificationGateway}, desacoplando el dominio de Firebase.
 * En modo local se inyectan las implementaciones NoOp.</p>
 */
@Component
public class MensajeEventListener {

    private static final Logger log = LoggerFactory.getLogger(MensajeEventListener.class);

    private final ChatGateway chatGateway;
    private final PushNotificationGateway pushGateway;

    public MensajeEventListener(ChatGateway chatGateway,
                                PushNotificationGateway pushGateway) {
        this.chatGateway = chatGateway;
        this.pushGateway = pushGateway;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMensajeNuevo(MensajeNuevoEvent event) {
        Mensaje mensaje = event.getMensaje();
        Usuario receiver = mensaje.getReceiver();
        Usuario sender = mensaje.getSender();

        if (receiver == null) return;

        Long senderId = sender != null ? sender.getIdUsuario() : null;
        Long receiverId = receiver.getIdUsuario();
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

        if (receiver.getFcmToken() != null && !receiver.getFcmToken().isBlank()) {
            String nombreRemitente = (sender != null)
                    ? sender.getNombre() + " " + sender.getApellido()
                    : "Alguien";
            pushGateway.sendPush(
                    receiver.getFcmToken(),
                    "Nuevo mensaje de " + nombreRemitente,
                    mensaje.getMensaje());
        }
    }
}
