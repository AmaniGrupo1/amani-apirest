package com.amani.amaniapirest.gateway;

import java.util.concurrent.CompletableFuture;

/**
 * Abstracción del gateway de chat en tiempo real.
 *
 * <p>Desacopla la capa de dominio de la implementación concreta
 * (Firebase RTDB, emulador, o no-op para desarrollo local).</p>
 */
public interface ChatGateway {

    /**
     * Envía un mensaje al canal de tiempo real.
     *
     * @param chatId      identificador de la sala de chat
     * @param idSender    ID del remitente
     * @param idReceiver  ID del destinatario
     * @param mensaje     contenido del mensaje
     * @param enviadoEn   timestamp de envío (ISO-8601)
     * @param idMensaje   ID del mensaje persistido en PostgreSQL
     * @param leido       si el mensaje ya fue leído
     * @param idCita      ID de la cita asociada (puede ser null)
     */
    void sendMessage(String chatId, Long idSender, Long idReceiver,
                     String mensaje, String enviadoEn,
                     Long idMensaje, boolean leido, Long idCita);

    /**
     * Recupera los últimos mensajes de una conversación.
     *
     * @param chatId identificador de la sala de chat
     * @param limit  número máximo de mensajes a recuperar
     * @return CompletableFuture con el resultado de la consulta
     */
    CompletableFuture<ChatHistory> getRecentMessages(String chatId, int limit);

    /**
     * Genera un identificador estable para la sala de chat entre dos usuarios.
     *
     * @param user1Id identificador del primer usuario
     * @param user2Id identificador del segundo usuario
     * @return identificador estable de la sala de chat
     */
    String getConversationId(Long user1Id, Long user2Id);

    /**
     * Verifica que un usuario pertenezca a la conversación indicada.
     *
     * @param chatId identificador de la sala de chat
     * @param userId  identificador del usuario autenticado
     * @return true si el usuario es participante del chat
     */
    boolean userCanAccessChat(String chatId, Long userId);

    /**
     * Representa un snapshot del historial de chat recuperado del gateway.
     */
    record ChatHistory(String chatId, int messageCount, String rawJson) {}
}
