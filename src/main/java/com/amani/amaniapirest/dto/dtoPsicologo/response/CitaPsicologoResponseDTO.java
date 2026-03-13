package com.amani.amaniapirest.dto.dtoPsicologo.response;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte las citas asignadas a él.
 *
 * <p>Muestra la información de cada cita desde la perspectiva del psicólogo:
 * datos del paciente, fecha y hora, duración, estado y motivo. No incluye
 * datos internos de auditoría.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaPsicologoResponseDTO {

    /** Identificador único de la cita. */
    private Long idCita;

    /** Identificador del paciente que asiste a la cita. */
    private Long idPaciente;

    /** Nombre de pila del paciente. */
    private String nombrePaciente;

    /** Apellido del paciente. */
    private String apellidoPaciente;

    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    private Integer durationMinutes;

    /** Estado actual de la cita. */
    private String estadoCita;

    /** Motivo o descripción de la consulta. */
    private String motivo;
}

