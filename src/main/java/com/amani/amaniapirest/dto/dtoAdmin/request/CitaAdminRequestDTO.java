package com.amani.amaniapirest.dto.dtoAdmin.request;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para que el administrador cree o actualice una {@code Cita}.
 *
 * <p>Permite al administrador programar o modificar citas entre cualquier
 * paciente y psicólogo registrados, incluyendo el control del estado.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaAdminRequestDTO {

    /** Identificador del paciente que asistirá a la cita. */
    private Long idPaciente;

    /** Identificador del psicólogo que atenderá la cita. */
    private Long idPsicologo;

    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    private Integer durationMinutes;

    /**
     * Estado de la cita.
     * <p>El administrador puede asignar cualquier estado del ciclo de vida.</p>
     */
    private EstadoCita estado;

    /** Motivo o descripción de la consulta. */
    private String motivo;
}

