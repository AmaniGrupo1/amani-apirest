package com.amani.amaniapirest.dto.dtoAdmin.response;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la vista de administrador sobre una {@code Cita}.
 *
 * <p>Muestra la información completa de la cita, incluyendo los datos del
 * paciente y del psicólogo involucrados, el estado actual y los metadatos
 * de auditoría.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CitaAdminResponse", description = "Cita completa — vista administrador")
public class CitaAdminResponseDTO {

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

    /** Identificador del psicólogo que atiende la cita. */
    @Schema(description = "ID del psicólogo", example = "2")

    private Long idPsicologo;

    /** Nombre de pila del psicólogo. */
    @Schema(description = "Nombre del psicólogo", example = "Ana")

    private String nombrePsicologo;

    /** Apellido del psicólogo. */
    @Schema(description = "Apellido del psicólogo", example = "García")

    private String apellidoPsicologo;

    /** Fecha y hora de inicio de la cita. */
    @Schema(description = "Inicio de la cita", example = "2026-04-01T10:00:00")

    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    @Schema(description = "Duración en minutos", example = "60")

    private Integer durationMinutes;

    /** Estado actual de la cita. */
    @Schema(description = "Estado de la cita", example = "pendiente")

    private String estadoCita;

    /** Motivo o descripción de la consulta. */
    @Schema(description = "Motivo de la consulta", example = "Seguimiento")

    private String motivo;

    /** Fecha y hora de creación del registro. */
    @Schema(description = "Fecha de creación", example = "2026-03-15T08:00:00")

    private LocalDateTime createdAt;

    /** Fecha y hora de la última modificación del registro. */
    @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

    private LocalDateTime updatedAt;
}

