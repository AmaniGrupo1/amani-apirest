package com.amani.amaniapirest.services;

import com.amani.amaniapirest.models.Mensaje;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Servicio que gestiona la interacción con Firebase Realtime Database para chat en tiempo real.
 *
 * <p>Escribe mensajes en RTDB para entrega en tiempo real y permite recuperar
 * el historial reciente de una conversación. Solo se instancia si {@link FirebaseApp}
 * está disponible.</p>
 *
 * <p>Estructura en RTDB: {@code /chats/{chatId}/messages/{pushId}}</p>
 */
@Service
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseChatService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseChatService.class);

    private final FirebaseApp firebaseApp;
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseNotificationService notificationService;

    public FirebaseChatService(FirebaseApp firebaseApp,
                               FirebaseDatabase firebaseDatabase,
                               FirebaseNotificationService notificationService) {
        this.firebaseApp = firebaseApp;
        this.firebaseDatabase = firebaseDatabase;
        this.notificationService = notificationService;
    }

    // ----------------------------------------------------------------
    // Generación de chatId
    // ----------------------------------------------------------------

    /**
     * Genera un identificador estable para la sala de chat entre dos usuarios.
     *
     * <p>El formato es {@code {minId}_{maxId}} para que ambos usuarios obtengan
     * el mismo chatId independientemente del orden de los parámetros.</p>
     *
     * @param user1Id identificador del primer usuario
     * @param user2Id identificador del segundo usuario
     * @return identificador estable de la sala de chat
     */
    public String getConversationId(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Los IDs de usuario no pueden ser nulos");
        }
        long min = Math.min(user1Id, user2Id);
        long max = Math.max(user1Id, user2Id);
        return min + "_" + max;
    }

    // ----------------------------------------------------------------
    // Escritura
    // ----------------------------------------------------------------

    /**
     * Envía (escribe) el mensaje en Realtime Database. Operación asíncrona.
     *
     * <p>El mensaje se escribe en {@code /chats/{chatId}/messages} y se envía
     * una notificación push al receptor si tiene token FCM.</p>
     *
     * @param mensaje entidad Mensaje ya persistida en PostgreSQL
     */
    @Async
    public void enviarMensaje(Mensaje mensaje) {
        try {
            Long s = mensaje.getSender() != null ? mensaje.getSender().getIdUsuario() : null;
            Long r = mensaje.getReceiver() != null ? mensaje.getReceiver().getIdUsuario() : null;
            if (s == null || r == null) {
                log.warn("[RTDB] Mensaje sin remitente o receptor, se omite RTDB: idSender={} idReceiver={}", s, r);
                return;
            }

            String roomId = getConversationId(s, r);

            DatabaseReference ref = firebaseDatabase.getReference("chats").child(roomId).child("messages");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idMensaje", mensaje.getIdMensaje());
            payload.put("idSender", s);
            payload.put("idReceiver", r);
            payload.put("mensaje", mensaje.getMensaje());
            payload.put("enviadoEn", mensaje.getEnviadoEn() != null ? mensaje.getEnviadoEn().toString() : null);
            payload.put("leido", mensaje.isLeido());
            if (mensaje.getCita() != null) {
                payload.put("idCita", mensaje.getCita().getIdCita());
            }

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

    // ----------------------------------------------------------------
    // Lectura
    // ----------------------------------------------------------------

    /**
     * Recupera los últimos {@code limit} mensajes de una conversación desde RTDB.
     *
     * <p>Este método accede directamente a Firebase Realtime Database. Para evitar
     * lecturas frecuentes, considerar el uso de caché en la capa superior.</p>
     *
     * @param chatId identificador de la sala de chat
     * @param limit  número máximo de mensajes a recuperar
     * @return CompletableFuture con el DataSnapshot de los mensajes
     */
    public CompletableFuture<DataSnapshot> getRecentMessages(String chatId, int limit) {
        try {
            Query query = firebaseDatabase
                    .getReference("chats")
                    .child(chatId)
                    .child("messages")
                    .orderByChild("enviadoEn")
                    .limitToLast(limit);

            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    log.debug("[RTDB] Recuperados {} mensajes para chatId={}", snapshot.getChildrenCount(), chatId);
                    future.complete(snapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    log.error("[RTDB] Error leyendo mensajes de RTDB para chatId={}: {}", chatId, error.getMessage());
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });

            return future.orTimeout(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("[RTDB] Error accediendo a RTDB para chatId={}: {}", chatId, ex.getMessage(), ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    // ----------------------------------------------------------------
    // Validación de acceso
    // ----------------------------------------------------------------

    /**
     * Verifica que un usuario pertenezca a la conversación indicada.
     *
     * <p>Un usuario solo puede acceder a chats donde es uno de los dos participantes.
     * El chatId tiene formato {@code {minId}_{maxId}}, por lo que se puede extraer
     * los IDs de los participantes y compararlos con el userId.</p>
     *
     * @param chatId identificador de la sala de chat
     * @param userId identificador del usuario autenticado
     * @return true si el usuario es participante del chat
     */
    public boolean userCanAccessChat(String chatId, Long userId) {
        if (chatId == null || userId == null) {
            return false;
        }
        String[] parts = chatId.split("_");
        if (parts.length != 2) {
            return false;
        }
        try {
            long user1 = Long.parseLong(parts[0]);
            long user2 = Long.parseLong(parts[1]);
            return userId == user1 || userId == user2;
        } catch (NumberFormatException e) {
            log.warn("[RTDB] chatId con formato inválido: {}", chatId);
            return false;
        }
    }
}
