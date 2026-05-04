package com.amani.amaniapirest.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para enviar un mensaje en una conversación de chat.
 *
 * <p>Se usa en los endpoints {@code /api/chats/{chatId}/messages}.
 * El {@code idSender} se toma del usuario autenticado, no del cuerpo de la petición,
 * pero se mantiene opcional para compatibilidad con el flujo existente.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SendMessageRequest", description = "Datos para enviar un mensaje en un chat")
public class SendMessageRequestDTO {

    /** Identificador del usuario que envía el mensaje. */
    @NotNull
    @Schema(description = "ID del remitente", example = "1")
    private Long idSender;

    /** Identificador del usuario que recibe el mensaje. */
    @NotNull
    @Schema(description = "ID del destinatario", example = "2")
    private Long idReceiver;

    /** Contenido textual del mensaje. */
    @NotBlank
    @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo te encuentras hoy?")
    private String mensaje;

    /** Identificador de la cita relacionada, si aplica. */
    @Schema(description = "ID de cita relacionada, si aplica", example = "5")
    private Long idCita;
}
