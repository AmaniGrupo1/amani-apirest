package com.amani.amaniapirest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con la información de una cita.
 *
 * <p>Incluye los identificadores del paciente y el psicólogo, la fecha y
 * hora de inicio, la duración, el estado y el motivo de la consulta.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaResponseDTO {

    /** Identificador unico de la cita. */
    private Long idCita;

    /** Identificador del paciente que asiste a la cita. */
    private Long idPaciente;

    /** Identificador del psicólogo que atiende la cita. */
    private Long idPsicologo;

    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    private Integer durationMinutes;

    /** Estado actual de la cita (pendiente, confirmada, cancelada, completada). */
    private String estado;

    /** Motivo o descripción de la consulta. */
    private String motivo;
}
