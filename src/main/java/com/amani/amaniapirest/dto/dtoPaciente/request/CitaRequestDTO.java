package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.enums.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para crear o actualizar una cita.
 *
 * <p>Los campos {@code idPaciente}, {@code idPsicologo} y {@code startDatetime}
 * son obligatorios. Si no se indica estado, el servicio lo establece en
 * {@code pendiente} por defecto.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CitaRequest", description = "Datos para crear o actualizar una cita")
public class CitaRequestDTO {

    /** Identificador del paciente que solicita la cita. */
    @NotNull
    @Schema(description = "Identificador del paciente que solicita la cita", example = "1")
    private Long idPaciente;

    /** Identificador del psicólogo asignado a la cita. */
    @NotNull
    @Schema(description = "Identificador del psicólogo asignado a la cita", example = "2")
    private Long idPsicologo;

    /** Fecha y hora de inicio de la cita. */
    @NotNull
    @Schema(description = "Fecha y hora de inicio de la cita", example = "2026-04-01T10:00:00")
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. Opcional; valor por defecto 0. */
    @Schema(description = "Duración de la cita en minutos; por defecto 0", example = "60")
    private Integer durationMinutes;

    /** Estado de la cita (pendiente, confirmada, cancelada, completada). Opcional. */
    @Schema(description = "Estado de la cita (pendiente, confirmada, cancelada, completada)", example = "pendiente")
    private EstadoCita estado;

    /** Motivo o descripción de la consulta. */
    @Schema(description = "Motivo o descripción de la consulta", example = "Sesión de seguimiento semanal")
    private String motivo;
}
