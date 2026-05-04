package com.amani.amaniapirest.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para representar una conversación de chat en la lista de conversaciones de un usuario.
 *
 * <p>Cada conversación se identifica por su {@code chatId} y muestra el último mensaje
 * y los datos básicos del otro participante.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ChatConversation", description = "Resumen de una conversación de chat")
public class ChatConversationDTO {

    /** Identificador estable de la sala de chat ({@code {minId}_{maxId}}). */
    @Schema(description = "ID de la conversación (formato minId_maxId)", example = "1_2")
    private String chatId;

    /** Identificador del otro participante en la conversación. */
    @Schema(description = "ID del otro participante", example = "2")
    private Long otherUserId;

    /** Nombre del otro participante. */
    @Schema(description = "Nombre del otro participante", example = "Dr. García")
    private String otherUserName;

    /** Último mensaje de la conversación. */
    @Schema(description = "Último mensaje de la conversación", example = "Hasta luego")
    private String lastMessage;

    /** Fecha del último mensaje. */
    @Schema(description = "Fecha del último mensaje", example = "2026-05-04T14:30:00")
    private LocalDateTime lastMessageAt;

    /** Indica si el último mensaje fue leído. */
    @Schema(description = "Si el último mensaje fue leído", example = "false")
    private Boolean lastMessageRead;

    /** Total de mensajes no leídos en esta conversación. */
    @Schema(description = "Mensajes no leídos en la conversación", example = "3")
    private Integer unreadCount;
}
