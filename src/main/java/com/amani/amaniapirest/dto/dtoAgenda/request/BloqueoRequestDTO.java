package com.amani.amaniapirest.dto.dtoAgenda.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la solicitud de bloqueo de horario en la agenda.
 *
 * <p>Utilizado para reservar un intervalo de tiempo en la agenda de un psicólogo,
 * impidiendo que se programen citas durante ese periodo.</p>
 *
 * @param fecha      fecha del bloqueo en formato ISO (AAAA-MM-DD)
 * @param horaInicio hora de inicio del bloqueo en formato HH:mm
 * @param horaFin    hora de finalización del bloqueo en formato HH:mm
 * @param motivo     descripción del motivo del bloqueo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueoRequestDTO {
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String motivo;
}
