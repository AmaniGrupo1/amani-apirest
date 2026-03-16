package com.amani.amaniapirest.dto.dtoPsicologo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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