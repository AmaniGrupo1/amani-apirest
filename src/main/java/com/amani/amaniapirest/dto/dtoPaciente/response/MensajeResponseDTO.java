package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida con la información de un mensaje entre usuarios.
 *
 * <p>Incluye los identificadores del remitente y destinatario, el contenido
 * del mensaje, la cita relacionada si existe, la fecha de envío y si fue leído.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MensajeResponse", description = "Mensaje entre usuarios — vista paciente")
public class MensajeResponseDTO {

    /** Nombre del usuario que envió el mensaje. */
    @Schema(description = "Nombre del remitente", example = "Dr. García")

    private String nombreRemitente;
    /** Contenido textual del mensaje. */
    @Schema(description = "Contenido del mensaje", example = "Recuerda tu cita mañana")

    private String mensaje;
    /** Fecha y hora en que el mensaje fue enviado. */
    @Schema(description = "Fecha y hora de envío", example = "2026-03-19T14:00:00")

    private LocalDateTime enviadoEn;

    /** Indica si el mensaje fue leído por el destinatario. */
    @Schema(description = "Si fue leído por el destinatario", example = "false")

    private Boolean leido;
}
