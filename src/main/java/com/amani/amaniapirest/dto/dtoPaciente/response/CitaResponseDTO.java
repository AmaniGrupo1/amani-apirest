package com.amani.amaniapirest.dto.dtoPaciente.response;

import com.amani.amaniapirest.enums.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "CitaResponse", description = "Información de una cita — vista paciente")
public class CitaResponseDTO {

    /** Identificador único de la cita. */
    @Schema(description = "Identificador único de la cita", example = "10")
    private Long idCita;

    /** Identificador del paciente que asiste a la cita. */
    @Schema(description = "Identificador del paciente", example = "1")
    private Long idPaciente;

    /** Identificador del psicólogo que atiende la cita. */
    @Schema(description = "Identificador del psicólogo", example = "2")
    private Long idPsicologo;

    /** Fecha y hora de inicio de la cita. */
    @Schema(description = "Fecha y hora de inicio de la cita", example = "2026-04-01T10:00:00")
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    @Schema(description = "Duración de la cita en minutos", example = "60")
    private Integer durationMinutes;

    /** Estado actual de la cita; por defecto {@code pendiente} al crearse. */
    @Schema(description = "Estado actual de la cita", example = "pendiente")
    private EstadoCita estadoCita;

    /** Motivo o descripción de la consulta. */
    @Schema(description = "Motivo o descripción de la consulta", example = "Sesión de seguimiento semanal")
    private String motivo;
}
