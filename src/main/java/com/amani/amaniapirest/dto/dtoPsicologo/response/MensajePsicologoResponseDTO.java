package com.amani.amaniapirest.dto.dtoPsicologo.response;


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
public class MensajePsicologoResponseDTO {

    /** Nombre del usuario remitente */
    private String nombreSender;

    /** Nombre del usuario destinatario */
    private String nombreReceiver;

    /** Contenido del mensaje */
    private String mensaje;

    /** Fecha y hora de envío */
    private LocalDateTime enviadoEn;

    /** Estado de lectura */
    private Boolean leido;
}