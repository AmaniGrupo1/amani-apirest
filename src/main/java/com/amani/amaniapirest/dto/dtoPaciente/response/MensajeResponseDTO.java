package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con la información de un mensaje entre usuarios.
 *
 * <p>Incluye los identificadores del remitente y destinatario, el contenido
 * del mensaje, la cita relacionada si existe, la fecha de envío y si fue leído.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensajeResponseDTO {

    /** Nombre del usuario que envió el mensaje. */
    private String nombreRemitente;
    /** Contenido textual del mensaje. */
    private String mensaje;
    /** Fecha y hora en que el mensaje fue enviado. */
    private LocalDateTime enviadoEn;

    /** Indica si el mensaje fue leído por el destinatario. */
    private Boolean leido;
}
