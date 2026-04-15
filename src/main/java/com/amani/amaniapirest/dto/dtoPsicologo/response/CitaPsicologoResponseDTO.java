package com.amani.amaniapirest.dto.dtoPsicologo.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "CitaPsicologoResponse", description = "Cita asignada — vista psicólogo")
public class CitaPsicologoResponseDTO {

    /** Identificador único de la cita. */
    @Schema(description = "Identificador de la cita", example = "1")

    private Long idCita;

    /** Identificador del paciente que asiste a la cita. */
    @Schema(description = "Identificador del paciente", example = "1")

    private Long idPaciente;

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

    /** Fecha y hora de inicio de la cita. */
    @Schema(description = "Inicio de la cita", example = "2026-04-01T10:00:00")

    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    @Schema(description = "Duración en minutos", example = "60")

    private Integer durationMinutes;

    /** Estado actual de la cita. */
    @Schema(description = "Estado de la cita", example = "confirmada")

    private String estadoCita;

    /** Motivo o descripción de la consulta. */
    @Schema(description = "Motivo de la consulta", example = "Seguimiento")

    private String motivo;
}

