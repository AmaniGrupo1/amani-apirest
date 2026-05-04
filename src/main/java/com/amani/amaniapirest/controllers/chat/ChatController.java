package com.amani.amaniapirest.controllers.chat;

import com.amani.amaniapirest.dto.chat.ChatConversationDTO;
import com.amani.amaniapirest.dto.chat.ChatMessageDTO;
import com.amani.amaniapirest.dto.chat.SendMessageRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.MensajeRequestDTO;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import com.amani.amaniapirest.services.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la funcionalidad de chat en tiempo real.
 *
 * <p>Expone endpoints para enviar mensajes, recuperar historial por conversación
 * y listar las conversaciones del usuario autenticado. La fuente canónica es
 * PostgreSQL; Firebase RTDB se usa como canal de entrega en tiempo real.</p>
 *
 * <p>Base path: {@code /api/chats}</p>
 */
@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chat", description = "Gestión de conversaciones de chat en tiempo real")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final UsuarioRepository usuarioRepository;

    public ChatController(ChatService chatService, UsuarioRepository usuarioRepository) {
        this.chatService = chatService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Envía un mensaje en una conversación de chat.
     */
    @Operation(summary = "Enviar mensaje", description = "Envía un mensaje en una conversación de chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensaje creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @Valid @RequestBody SendMessageRequestDTO request) {
        log.debug("[Chat] Enviando mensaje de {} a {}", request.getIdSender(), request.getIdReceiver());

        MensajeRequestDTO mensajeRequest = new MensajeRequestDTO();
        mensajeRequest.setIdSender(request.getIdSender());
        mensajeRequest.setIdReceiver(request.getIdReceiver());
        mensajeRequest.setMensaje(request.getMensaje());
        mensajeRequest.setIdCita(request.getIdCita());

        ChatMessageDTO created = chatService.sendMessage(mensajeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Envía un mensaje en una conversación específica identificada por chatId.
     */
    @Operation(summary = "Enviar mensaje en chat", description = "Envía un mensaje en una conversación específica por chatId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensaje creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o chatId incorrecto", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para acceder a esta conversación", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ChatMessageDTO> sendMessageToChat(
            @PathVariable String chatId,
            @Valid @RequestBody SendMessageRequestDTO request) {
        log.debug("[Chat] Enviando mensaje en chatId={}", chatId);

        String[] parts = chatId.split("_");
        if (parts.length != 2) {
            return ResponseEntity.badRequest().build();
        }

        MensajeRequestDTO mensajeRequest = new MensajeRequestDTO();
        mensajeRequest.setIdSender(request.getIdSender());
        mensajeRequest.setIdReceiver(request.getIdReceiver());
        mensajeRequest.setMensaje(request.getMensaje());
        mensajeRequest.setIdCita(request.getIdCita());

        ChatMessageDTO created = chatService.sendMessage(mensajeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Recupera los mensajes de una conversación de chat.
     */
    @Operation(summary = "Historial de chat", description = "Recupera los mensajes recientes de una conversación de chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensajes recuperados correctamente"),
            @ApiResponse(responseCode = "400", description = "chatId con formato inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para acceder a esta conversación", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(
            @PathVariable String chatId,
            @Parameter(description = "Número máximo de mensajes a devolver (por defecto 50)")
            @RequestParam(defaultValue = "50") int limit) {
        log.debug("[Chat] Recuperando mensajes de chatId={}, limit={}", chatId, limit);

        List<ChatMessageDTO> messages = chatService.getConversationMessages(chatId, limit);
        return ResponseEntity.ok(messages);
    }

    /**
     * Lista las conversaciones de chat del usuario autenticado.
     *
     * <p>Extrae el email del principal JWT, lo resuelve a un userId
     * usando {@code UsuarioRepository}, y devuelve las conversaciones.</p>
     *
     * @param userDetails usuario autenticado (inyectado por Spring Security)
     * @return lista de conversaciones del usuario
     */
    @Operation(summary = "Lista de conversaciones", description = "Recupera las conversaciones de chat del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversaciones recuperadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversationDTO>> getUserConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Long userId = usuarioRepository.findByEmail(email)
                .map(Usuario::getIdUsuario)
                .orElse(null);

        if (userId == null) {
            log.warn("[Chat] Usuario autenticado con email {} no encontrado en BD", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("[Chat] Recuperando conversaciones para userId={} (email={})", userId, email);
        List<ChatConversationDTO> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Lista las conversaciones de chat de un usuario por su ID.
     *
     * <p>Versión del endpoint que recibe el userId como parámetro de ruta.</p>
     *
     * @param userId identificador del usuario
     * @return lista de conversaciones del usuario
     */
    @Operation(summary = "Conversaciones por userId", description = "Recupera las conversaciones de chat de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversaciones recuperadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ChatConversationDTO>> getUserConversationsByUserId(
            @PathVariable Long userId) {
        log.debug("[Chat] Recuperando conversaciones para userId={}", userId);
        List<ChatConversationDTO> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }
}
