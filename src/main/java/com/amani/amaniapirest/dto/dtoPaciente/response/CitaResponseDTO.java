package com.amani.amaniapirest.dto.dtoPaciente.response;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con la información de una cita desde la perspectiva del paciente.
 *
 * <p>Expone los identificadores del paciente y del psicólogo, la fecha, duración,
 * estado actual y motivo de la consulta.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaResponseDTO {

    /** Identificador único de la cita. */
    private Long idCita;

    /** Identificador del paciente que asiste a la cita. */
    private Long idPaciente;

    /** Identificador del psicólogo que atiende la cita. */
    private Long idPsicologo;

    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    private Integer durationMinutes;

    /** Estado actual de la cita; por defecto {@code pendiente} al crearse. */
    private EstadoCita estadoCita;

    /** Motivo o descripción de la consulta. */
    private String motivo;
}
