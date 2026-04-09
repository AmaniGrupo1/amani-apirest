package com.amani.amaniapirest.services;

import com.amani.amaniapirest.models.Mensaje;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio que escribe los mensajes en Firebase Realtime Database para entrega en tiempo real
 * a clientes que escuchan la ruta /chats/{roomId}/messages.
 */
@Service
public class FirebaseChatService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseChatService.class);

    private final FirebaseApp firebaseApp;
    private final FirebaseNotificationService notificationService;

    public FirebaseChatService(FirebaseApp firebaseApp, FirebaseNotificationService notificationService) {
        this.firebaseApp = firebaseApp;
        this.notificationService = notificationService;
    }

    /**
     * Envía (escribe) el mensaje en Realtime Database. Operación asíncrona.
     */
    @Async
    public void enviarMensaje(Mensaje mensaje) {
        try {
            // Construir ID de sala estable a partir de los dos IDs (ordenados)
            Long s = mensaje.getSender() != null ? mensaje.getSender().getIdUsuario() : null;
            Long r = mensaje.getReceiver() != null ? mensaje.getReceiver().getIdUsuario() : null;
            if (s == null || r == null) {
                log.warn("[RTDB] Mensaje sin remitente o receptor, se omite RTDB: idSender={} idReceiver={}", s, r);
                return;
            }

            String roomId = (s <= r) ? s + "_" + r : r + "_" + s;

            FirebaseDatabase db = FirebaseDatabase.getInstance(firebaseApp);
            DatabaseReference ref = db.getReference("chats").child(roomId).child("messages");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idMensaje", mensaje.getIdMensaje());
            payload.put("idSender", s);
            payload.put("idReceiver", r);
            payload.put("mensaje", mensaje.getMensaje());
            payload.put("enviadoEn", mensaje.getEnviadoEn() != null ? mensaje.getEnviadoEn().toString() : null);
            payload.put("leido", mensaje.isLeido());
            if (mensaje.getCita() != null) payload.put("idCita", mensaje.getCita().getIdCita());

            ref.push().setValueAsync(payload);

            // Además, enviar push al receptor si tiene token
            if (mensaje.getReceiver() != null && mensaje.getReceiver().getFcmToken() != null) {
                notificationService.enviarPush(mensaje.getReceiver().getFcmToken(), "Nuevo mensaje", mensaje.getMensaje());
            }

            log.debug("[RTDB] Mensaje escrito en room {}: idMensaje={}", roomId, mensaje.getIdMensaje());
        } catch (Exception ex) {
            log.error("[RTDB] Error escribiendo mensaje en RTDB: {}", ex.getMessage(), ex);
        }
    }
}

