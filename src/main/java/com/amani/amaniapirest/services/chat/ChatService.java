package com.amani.amaniapirest.services.chat;

import com.amani.amaniapirest.dto.chat.ChatConversationDTO;
import com.amani.amaniapirest.dto.chat.ChatMessageDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.events.MensajeNuevoEvent;
import com.amani.amaniapirest.models.Mensaje;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.MensajeRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de dominio para la funcionalidad de chat.
 *
 * <p>Orquesta la comunicación entre PostgreSQL (canónico) y Firebase RTDB (tiempo real).
 * La publicación a RTDB se realiza de forma única a través del evento de dominio
 * {@link MensajeNuevoEvent}, procesado por {@code MensajeEventListener}.</p>
 *
 * <p>Utiliza caché Caffeine ({@code chatMessages}) para reducir lecturas repetitivas
 * de historial reciente. La caché se invalida automáticamente al enviar un nuevo mensaje.</p>
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ChatService(MensajeRepository mensajeRepository,
                       UsuarioRepository usuarioRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    // ----------------------------------------------------------------
    // Envío de mensajes
    // ----------------------------------------------------------------

    /**
     * Envía un mensaje en una conversación de chat.
     *
     * <p>Persiste el mensaje en PostgreSQL y publica {@link MensajeNuevoEvent}
     * para que {@code MensajeEventListener} lo escriba en Firebase RTDB.
     * Esta es la única vía de publicación a RTDB — no se escribe directamente aquí.</p>
     *
     * @param request datos del mensaje a enviar
     * @return DTO con los datos del mensaje creado
     * @throws IllegalArgumentException si el remitente o destinatario no existen
     */
    @Transactional
    @CacheEvict(value = "chatMessages", key = "#request.idSender + '_' + #request.idReceiver")
    public ChatMessageDTO sendMessage(MensajeRequestDTO request) {
        Usuario sender = usuarioRepository.findById(request.getIdSender())
                .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado con id: " + request.getIdSender()));
        Usuario receiver = usuarioRepository.findById(request.getIdReceiver())
                .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado con id: " + request.getIdReceiver()));

        Mensaje mensaje = new Mensaje();
        mensaje.setSender(sender);
        mensaje.setReceiver(receiver);
        mensaje.setMensaje(request.getMensaje());
        mensaje.setEnviadoEn(LocalDateTime.now());
        mensaje.setLeido(false);

        Mensaje saved = mensajeRepository.save(mensaje);

        // Única vía de publicación a RTDB: evento de dominio → MensajeEventListener
        eventPublisher.publishEvent(new MensajeNuevoEvent(this, saved));
        log.debug("[Chat] Mensaje persistido id={} y evento MensajeNuevoEvent publicado para entrega RTDB.", saved.getIdMensaje());

        return toChatMessageDTO(saved);
    }

    // ----------------------------------------------------------------
    // Lectura de historial
    // ----------------------------------------------------------------

    /**
     * Recupera el historial de mensajes de una conversación.
     *
     * <p>Lee de PostgreSQL (canónico) y cachea el resultado para reducir lecturas
     * repetitivas. El TTL de la caché es de 30 segundos.</p>
     *
     * @param chatId identificador de la sala de chat ({@code {minId}_{maxId}})
     * @param limit  número máximo de mensajes a devolver (por defecto 50)
     * @return lista de {@link ChatMessageDTO} con los mensajes de la conversación
     * @throws IllegalArgumentException si el chatId tiene formato inválido
     */
    @Cacheable(value = "chatMessages", key = "#chatId + '_' + #limit")
    public List<ChatMessageDTO> getConversationMessages(String chatId, int limit) {
        String[] parts = parseChatId(chatId);
        Long userId1 = Long.parseLong(parts[0]);
        Long userId2 = Long.parseLong(parts[1]);

        List<Mensaje> messages = mensajeRepository.findRecentConversationMessages(userId1, userId2);

        if (limit > 0 && messages.size() > limit) {
            messages = messages.subList(0, limit);
        }

        messages = new ArrayList<>(messages);
        Collections.reverse(messages);

        return messages.stream()
                .map(this::toChatMessageDTO)
                .toList();
    }

    // ----------------------------------------------------------------
    // Lista de conversaciones
    // ----------------------------------------------------------------

    /**
     * Recupera la lista de conversaciones de un usuario.
     *
     * @param userId identificador del usuario autenticado
     * @return lista de {@link ChatConversationDTO} con las conversaciones del usuario
     */
    public List<ChatConversationDTO> getUserConversations(Long userId) {
        List<Mensaje> enviados = mensajeRepository.findBySender_IdUsuario(userId);
        List<Mensaje> recibidos = mensajeRepository.findByReceiver_IdUsuario(userId);

        Map<Long, Mensaje> lastMessagesByUser = new LinkedHashMap<>();

        for (Mensaje m : enviados) {
            Long otherId = m.getReceiver().getIdUsuario();
            lastMessagesByUser.merge(otherId, m, (a, b) ->
                    a.getEnviadoEn().isAfter(b.getEnviadoEn()) ? a : b);
        }
        for (Mensaje m : recibidos) {
            Long otherId = m.getSender().getIdUsuario();
            lastMessagesByUser.merge(otherId, m, (a, b) ->
                    a.getEnviadoEn().isAfter(b.getEnviadoEn()) ? a : b);
        }

        List<ChatConversationDTO> conversations = new ArrayList<>();
        for (Map.Entry<Long, Mensaje> entry : lastMessagesByUser.entrySet()) {
            Long otherUserId = entry.getKey();
            Mensaje lastMsg = entry.getValue();

            long min = Math.min(userId, otherUserId);
            long max = Math.max(userId, otherUserId);
            String chatId = min + "_" + max;

            long unreadCount = recibidos.stream()
                    .filter(m -> m.getSender().getIdUsuario().equals(otherUserId) && !m.isLeido())
                    .count();

            String otherName = lastMsg.getSender().getIdUsuario().equals(otherUserId)
                    ? lastMsg.getSender().getNombre() + " " + lastMsg.getSender().getApellido()
                    : lastMsg.getReceiver().getNombre() + " " + lastMsg.getReceiver().getApellido();

            conversations.add(ChatConversationDTO.builder()
                    .chatId(chatId)
                    .otherUserId(otherUserId)
                    .otherUserName(otherName)
                    .lastMessage(lastMsg.getMensaje())
                    .lastMessageAt(lastMsg.getEnviadoEn())
                    .lastMessageRead(lastMsg.isLeido())
                    .unreadCount((int) unreadCount)
                    .build());
        }

        conversations.sort((a, b) -> {
            if (a.getLastMessageAt() == null && b.getLastMessageAt() == null) return 0;
            if (a.getLastMessageAt() == null) return 1;
            if (b.getLastMessageAt() == null) return -1;
            return b.getLastMessageAt().compareTo(a.getLastMessageAt());
        });

        return conversations;
    }

    // ----------------------------------------------------------------
    // Validación de acceso
    // ----------------------------------------------------------------

    /**
     * Verifica que un usuario pueda acceder a una conversación de chat.
     *
     * @param chatId identificador de la sala de chat
     * @param userId identificador del usuario autenticado
     * @return true si el usuario es participante del chat
     */
    public boolean canAccessChat(String chatId, Long userId) {
        if (chatId == null || userId == null) return false;
        String[] parts = chatId.split("_");
        if (parts.length != 2) return false;
        try {
            long user1 = Long.parseLong(parts[0]);
            long user2 = Long.parseLong(parts[1]);
            return userId == user1 || userId == user2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Genera el chatId para una conversación entre dos usuarios.
     *
     * @param user1Id identificador del primer usuario
     * @param user2Id identificador del segundo usuario
     * @return identificador estable de la sala de chat
     */
    public String getConversationId(Long user1Id, Long user2Id) {
        long min = Math.min(user1Id, user2Id);
        long max = Math.max(user1Id, user2Id);
        return min + "_" + max;
    }

    // ----------------------------------------------------------------
    // Utilidades internas
    // ----------------------------------------------------------------

    private ChatMessageDTO toChatMessageDTO(Mensaje m) {
        return ChatMessageDTO.builder()
                .idMensaje(m.getIdMensaje())
                .idSender(m.getSender().getIdUsuario())
                .nombreSender(m.getSender().getNombre() + " " + m.getSender().getApellido())
                .idReceiver(m.getReceiver().getIdUsuario())
                .nombreReceiver(m.getReceiver().getNombre() + " " + m.getReceiver().getApellido())
                .mensaje(m.getMensaje())
                .enviadoEn(m.getEnviadoEn())
                .leido(m.isLeido())
                .idCita(m.getCita() != null ? m.getCita().getIdCita() : null)
                .build();
    }

    private String[] parseChatId(String chatId) {
        if (chatId == null || !chatId.contains("_")) {
            throw new IllegalArgumentException("Formato de chatId inválido: " + chatId);
        }
        String[] parts = chatId.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de chatId inválido: " + chatId);
        }
        try {
            Long.parseLong(parts[0]);
            Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("chatId contiene IDs no numéricos: " + chatId);
        }
        return parts;
    }
}
