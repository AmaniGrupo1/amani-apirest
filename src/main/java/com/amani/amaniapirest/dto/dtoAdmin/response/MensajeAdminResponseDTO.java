package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la vista de administrador sobre un mensaje entre usuarios.
 *
 * <p>Muestra los datos del remitente y destinatario (nombre y email), el contenido
 * del mensaje, la fecha de envío y el estado de lectura.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MensajeAdminResponse", description = "Mensaje entre usuarios — vista administrador")
public class MensajeAdminResponseDTO {
    /** Nombre del usuario remitente */
    @Schema(description = "Nombre del remitente", example = "Carlos")

    private String nombreSender;

    /** Email del usuario remitente */
    @Schema(description = "Email del remitente", example = "carlos@amani.com")

    private String emailSender;

    /** Nombre del usuario destinatario */
    @Schema(description = "Nombre del destinatario", example = "Dr. García")

    private String nombreReceiver;

    /** Email del usuario destinatario */
    @Schema(description = "Email del destinatario", example = "garcia@amani.com")

    private String emailReceiver;

    /** Contenido del mensaje */
    @Schema(description = "Contenido del mensaje", example = "Hola doctor")

    private String mensaje;


    /** Fecha y hora de envío */
    @Schema(description = "Fecha de envío", example = "2026-03-19T14:00:00")

    private LocalDateTime enviadoEn;

    /** Estado de lectura */
    @Schema(description = "Si fue leído", example = "false")

    private Boolean leido;
}
