package com.amani.amaniapirest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rastrea qué usuarios tienen actualmente una sesión WebSocket/STOMP abierta.
 *
 * <p>Escucha los eventos de ciclo de vida de las sesiones STOMP de Spring
 * ({@link SessionConnectedEvent} y {@link SessionDisconnectEvent}) para mantener
 * un conjunto de IDs de usuario conectados en memoria.</p>
 *
 * <h3>Convención de uso en el cliente</h3>
 * <p>El cliente debe enviar su {@code userId} como cabecera STOMP al conectarse:</p>
 * <pre>{@code
 *   stompClient.connect({ userId: '42' }, frame => { ... });
 * }</pre>
 * <p>El servidor lee esa cabecera en los eventos de sesión para registrar/desregistrar al usuario.</p>
 */
@Component
public class WebSocketPresenceTracker {

    private static final Logger log = LoggerFactory.getLogger(WebSocketPresenceTracker.class);
    private static final String HEADER_USER_ID = "userId";

    /** Conjunto thread-safe de IDs de usuario con sesión WebSocket activa. */
    private final Set<Long> connectedUsers = ConcurrentHashMap.newKeySet();

    /**
     * Registra al usuario cuando establece su sesión STOMP.
     *
     * @param event evento de conexión STOMP de Spring
     */
    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        extractUserId(event.getMessage().getHeaders()).ifPresent(id -> {
            connectedUsers.add(id);
            log.debug("[WS] Usuario {} conectado. Total online: {}", id, connectedUsers.size());
        });
    }

    /**
     * Desregistra al usuario cuando cierra su sesión STOMP.
     *
     * @param event evento de desconexión STOMP de Spring
     */
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        extractUserId(event.getMessage().getHeaders()).ifPresent(id -> {
            connectedUsers.remove(id);
            log.debug("[WS] Usuario {} desconectado. Total online: {}", id, connectedUsers.size());
        });
    }

    public boolean isConnected(Long userId) {
        return userId != null && connectedUsers.contains(userId);
    }

    /**
     * Permite registrar manualmente un usuario (útil para tests o integraciones externas).
     *
     * @param userId identificador del usuario a registrar
     */
    public void register(Long userId) {
        if (userId != null) connectedUsers.add(userId);
    }

    /**
     * Permite desregistrar manualmente un usuario.
     *
     * @param userId identificador del usuario a desregistrar
     */
    public void unregister(Long userId) {
        if (userId != null) connectedUsers.remove(userId);
    }

    // ----------------------------------------------------------------
    // Helper privado
    // ----------------------------------------------------------------

    private java.util.Optional<Long> extractUserId(org.springframework.messaging.MessageHeaders headers) {
        try {
            Object raw = headers.get(HEADER_USER_ID);
            if (raw instanceof String s && !s.isBlank()) {
                return java.util.Optional.of(Long.parseLong(s));
            }
        } catch (NumberFormatException ex) {
            log.warn("[WS] Cabecera '{}' con valor no numérico: {}", HEADER_USER_ID, ex.getMessage());
        }
        return java.util.Optional.empty();
    }
}

