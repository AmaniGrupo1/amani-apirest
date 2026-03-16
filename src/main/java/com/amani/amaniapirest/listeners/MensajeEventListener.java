package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.MensajeNuevoEvent;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.FirebaseNotificationService;
import com.amani.amaniapirest.services.WebSocketPresenceTracker;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener del dominio {@link Mensaje}.
 *
 * <p>Al recibir un {@link MensajeNuevoEvent} decide la vía de entrega:</p>
 * <ul>
 *   <li>Si el destinatario tiene sesión WebSocket activa → entrega por STOMP
 *       al canal personal {@code /queue/mensajes}.</li>
 *   <li>Si está offline → envía notificación push via Firebase.</li>
 * </ul>
 */
@Component
public class MensajeEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseNotificationService firebaseService;
    private final WebSocketPresenceTracker presenceTracker;

    public MensajeEventListener(SimpMessagingTemplate messagingTemplate,
                                 FirebaseNotificationService firebaseService,
                                 WebSocketPresenceTracker presenceTracker) {
        this.messagingTemplate = messagingTemplate;
        this.firebaseService = firebaseService;
        this.presenceTracker = presenceTracker;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMensajeNuevo(MensajeNuevoEvent event) {
        Mensaje mensaje = event.getMensaje();
        Usuario receiver = mensaje.getReceiver();
        Usuario sender   = mensaje.getSender();

        if (receiver == null) return;

        Long receiverId = receiver.getIdUsuario();
        String nombreRemitente = (sender != null) ? sender.getNombre() + " " + sender.getApellido() : "Alguien";

        if (presenceTracker.isConnected(receiverId)) {
            // Usuario online → entrega en tiempo real por WebSocket
            MensajePayload payload = new MensajePayload(
                    mensaje.getIdMensaje(),
                    sender != null ? sender.getIdUsuario() : null,
                    nombreRemitente,
                    mensaje.getMensaje(),
                    mensaje.getEnviadoEn()
            );
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(receiverId),
                    "/queue/mensajes",
                    payload
            );
        } else {
            // Usuario offline → push notification
            firebaseService.enviarPush(
                    receiver.getFcmToken(),
                    "Nuevo mensaje de " + nombreRemitente,
                    mensaje.getMensaje()
            );
        }
    }

    // ----------------------------------------------------------------
    // DTO interno ligero para la entrega WebSocket
    // ----------------------------------------------------------------

    public record MensajePayload(
            Long idMensaje,
            Long idSender,
            String nombreSender,
            String contenido,
            java.time.LocalDateTime enviadoEn
    ) {}
}

