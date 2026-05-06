package com.amani.amaniapirest.gateway.firebase;

import com.amani.amaniapirest.gateway.ChatGateway;
import com.amani.amaniapirest.gateway.ChatGateway.ChatHistory;
import com.amani.amaniapirest.gateway.PushNotificationGateway;
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
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Implementación de {@link ChatGateway} usando Firebase Realtime Database.
 *
 * <p>Solo se registra cuando {@link FirebaseApp} está disponible.
 * Escribe mensajes en RTDB para entrega en tiempo real y permite
 * recuperar el historial reciente de una conversación.</p>
 */
@Service
@Primary
@ConditionalOnBean(FirebaseApp.class)
public class FirebaseChatGateway implements ChatGateway {

    private static final Logger log = LoggerFactory.getLogger(FirebaseChatGateway.class);

    private final FirebaseDatabase firebaseDatabase;
    private final PushNotificationGateway pushGateway;

    public FirebaseChatGateway(FirebaseDatabase firebaseDatabase,
                                PushNotificationGateway pushGateway) {
        this.firebaseDatabase = firebaseDatabase;
        this.pushGateway = pushGateway;
    }

    @Async
    @Override
    public void sendMessage(String chatId, Long idSender, Long idReceiver,
                             String mensaje, String enviadoEn,
                             Long idMensaje, boolean leido, Long idCita) {
        try {
            DatabaseReference ref = firebaseDatabase
                    .getReference("chats")
                    .child(chatId)
                    .child("messages");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idMensaje", idMensaje);
            payload.put("idSender", idSender);
            payload.put("idReceiver", idReceiver);
            payload.put("mensaje", mensaje);
            payload.put("enviadoEn", enviadoEn);
            payload.put("leido", leido);
            if (idCita != null) {
                payload.put("idCita", idCita);
            }

            ref.push().setValueAsync(payload);
            log.debug("[RTDB] Mensaje escrito en room {}: idMensaje={}", chatId, idMensaje);
        } catch (Exception ex) {
            log.error("[RTDB] Error escribiendo mensaje en RTDB para room {}: {}",
                    chatId, ex.getMessage(), ex);
        }
    }

    @Override
    public CompletableFuture<ChatHistory> getRecentMessages(String chatId, int limit) {
        try {
            Query query = firebaseDatabase
                    .getReference("chats")
                    .child(chatId)
                    .child("messages")
                    .orderByChild("enviadoEn")
                    .limitToLast(limit);

            CompletableFuture<ChatHistory> future = new CompletableFuture<>();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount();
                    log.debug("[RTDB] Recuperados {} mensajes para chatId={}", count, chatId);
                    future.complete(new ChatHistory(chatId, (int) count, null));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    log.error("[RTDB] Error leyendo mensajes para chatId={}: {}", chatId, error.getMessage());
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });

            return future.orTimeout(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("[RTDB] Error accediendo a RTDB para chatId={}: {}", chatId, ex.getMessage(), ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    @Override
    public String getConversationId(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Los IDs de usuario no pueden ser nulos");
        }
        long min = Math.min(user1Id, user2Id);
        long max = Math.max(user1Id, user2Id);
        return min + "_" + max;
    }

    @Override
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
