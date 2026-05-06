package com.amani.amaniapirest.gateway.noop;

import com.amani.amaniapirest.gateway.ChatGateway;
import com.amani.amaniapirest.gateway.ChatGateway.ChatHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementación no-op de {@link ChatGateway} para entornos locales y de test.
 *
 * <p>Se registra cuando {@code firebase.enabled=false} (o está ausente).
 * Los mensajes no se envían a ningún backend de chat en tiempo real.</p>
 */
@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpChatGateway implements ChatGateway {

    private static final Logger log = LoggerFactory.getLogger(NoOpChatGateway.class);

    @Override
    public void sendMessage(String chatId, Long idSender, Long idReceiver,
                             String mensaje, String enviadoEn,
                             Long idMensaje, boolean leido, Long idCita) {
        log.debug("[Chat NoOp] Mensaje omitido (Firebase deshabilitado). chatId={}, idMensaje={}", chatId, idMensaje);
    }

    @Override
    public CompletableFuture<ChatHistory> getRecentMessages(String chatId, int limit) {
        log.debug("[Chat NoOp] getRecentMessages omitido (Firebase deshabilitado). chatId={}", chatId);
        return CompletableFuture.completedFuture(new ChatHistory(chatId, 0, null));
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
            return false;
        }
    }
}
