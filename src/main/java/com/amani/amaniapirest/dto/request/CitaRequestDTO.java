package com.amani.amaniapirest.dto.request;

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
public class CitaRequestDTO {

    /** Identificador del paciente que solicita la cita. */
    @NotNull
    private Long idPaciente;

    /** Identificador del psicólogo asignado a la cita. */
    @NotNull
    private Long idPsicologo;

    /** Fecha y hora de inicio de la cita. */
    @NotNull
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. Opcional; valor por defecto 0. */
    private Integer durationMinutes;

    /** Estado de la cita (pendiente, confirmada, cancelada, completada). Opcional. */
    private String estado;

    /** Motivo o descripción de la consulta. */
    private String motivo;
}
