package com.amani.amaniapirest.listeners;

import com.amani.amaniapirest.events.MensajeNuevoEvent;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.services.FirebaseChatService;
import com.amani.amaniapirest.services.FirebaseNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener del dominio {@link Mensaje} adaptado para no usar WebSocket.
 *
 * <p>Al recibir un {@link MensajeNuevoEvent} escribe el mensaje en Firebase RTDB
 * para entrega en tiempo real a los clientes y desencadena notificación push.</p>
 */
@Component
public class MensajeEventListener {

    private final FirebaseChatService firebaseChatService;
    private final FirebaseNotificationService firebaseService;

    public MensajeEventListener(FirebaseChatService firebaseChatService,
                                FirebaseNotificationService firebaseService) {
        this.firebaseChatService = firebaseChatService;
        this.firebaseService = firebaseService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMensajeNuevo(MensajeNuevoEvent event) {
        Mensaje mensaje = event.getMensaje();
        Usuario receiver = mensaje.getReceiver();
        Usuario sender = mensaje.getSender();

        if (receiver == null) return;

        // Escribir en Realtime Database para entrega en tiempo real
        firebaseChatService.enviarMensaje(mensaje);

        // Además, enviar notificación push si tiene token
        if (receiver.getFcmToken() != null && !receiver.getFcmToken().isBlank()) {
            String nombreRemitente = (sender != null) ? sender.getNombre() + " " + sender.getApellido() : "Alguien";
            firebaseService.enviarPush(receiver.getFcmToken(), "Nuevo mensaje de " + nombreRemitente, mensaje.getMensaje());
        }
    }
}

