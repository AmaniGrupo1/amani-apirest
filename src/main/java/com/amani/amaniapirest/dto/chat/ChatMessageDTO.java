package com.amani.amaniapirest.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para un mensaje dentro de una conversación de chat.
 *
 * <p>Incluye los identificadores del remitente y destinatario, el contenido
 * del mensaje, la fecha de envío y el estado de lectura.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ChatMessage", description = "Mensaje dentro de una conversación de chat")
public class ChatMessageDTO {

    /** Identificador único del mensaje en PostgreSQL. */
    @Schema(description = "ID del mensaje", example = "42")
    private Long idMensaje;

    /** Identificador del usuario que envió el mensaje. */
    @Schema(description = "ID del remitente", example = "1")
    private Long idSender;

    /** Nombre del remitente. */
    @Schema(description = "Nombre del remitente", example = "Laura")
    private String nombreSender;

    /** Identificador del usuario que recibe el mensaje. */
    @Schema(description = "ID del destinatario", example = "2")
    private Long idReceiver;

    /** Nombre del destinatario. */
    @Schema(description = "Nombre del destinatario", example = "Dr. García")
    private String nombreReceiver;

    /** Contenido textual del mensaje. */
    @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo estás?")
    private String mensaje;

    /** Fecha y hora en que el mensaje fue enviado. */
    @Schema(description = "Fecha de envío", example = "2026-05-04T14:30:00")
    private LocalDateTime enviadoEn;

    /** Indica si el mensaje fue leído por el destinatario. */
    @Schema(description = "Si fue leído por el destinatario", example = "false")
    private Boolean leido;

    /** Identificador de la cita relacionada, si aplica. */
    @Schema(description = "ID de cita relacionada, si aplica", example = "5")
    private Long idCita;
}
