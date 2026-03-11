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

    /** Identificador único del mensaje. */
    private Long idMensaje;

    /** Identificador del usuario remitente. */
    private Long idSender;

    /** Identificador del usuario destinatario. */
    private Long idReceiver;

    /** Contenido textual del mensaje. */
    private String mensaje;

    /** Identificador de la cita relacionada, si aplica. */
    private Long idCita;

    /** Fecha y hora en que el mensaje fue enviado. */
    private LocalDateTime enviadoEn;

    /** Indica si el mensaje fue leído por el destinatario. */
    private Boolean leido;
}
