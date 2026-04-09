package com.amani.amaniapirest.dto.dtoPsicologo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte los mensajes de su bandeja de entrada o enviados.
 *
 * <p>Incluye los nombres del remitente y destinatario, el contenido del mensaje,
 * la fecha de envío y el estado de lectura.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MensajePsicologoResponse", description = "Mensaje — vista psicólogo")
public class MensajePsicologoResponseDTO {

    /** Nombre del usuario remitente */
    @Schema(description = "Nombre del remitente", example = "Laura")

    private String nombreSender;

    /** Nombre del usuario destinatario */
    @Schema(description = "Nombre del destinatario", example = "Dr. García")

    private String nombreReceiver;

    /** Contenido del mensaje */
    @Schema(description = "Contenido del mensaje", example = "Buenos días")

    private String mensaje;

    /** Fecha y hora de envío */
    @Schema(description = "Fecha de envío", example = "2026-03-19T14:00:00")

    private LocalDateTime enviadoEn;

    /** Estado de lectura */
    @Schema(description = "Si fue leído", example = "false")

    private Boolean leido;
}