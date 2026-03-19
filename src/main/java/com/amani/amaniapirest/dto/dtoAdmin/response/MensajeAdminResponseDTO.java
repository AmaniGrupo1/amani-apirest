package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre un mensaje entre usuarios.
 *
 * <p>Muestra los datos del remitente y destinatario (nombre y email), el contenido
 * del mensaje, la fecha de envío y el estado de lectura.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensajeAdminResponseDTO {
    /** Nombre del usuario remitente */
    private String nombreSender;

    /** Email del usuario remitente */
    private String emailSender;

    /** Nombre del usuario destinatario */
    private String nombreReceiver;

    /** Email del usuario destinatario */
    private String emailReceiver;

    /** Contenido del mensaje */
    private String mensaje;


    /** Fecha y hora de envío */
    private LocalDateTime enviadoEn;

    /** Estado de lectura */
    private Boolean leido;
}
