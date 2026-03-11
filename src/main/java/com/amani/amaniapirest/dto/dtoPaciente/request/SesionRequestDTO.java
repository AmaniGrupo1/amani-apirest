package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para crear o modificar una sesión terapéutica.
 *
 * <p>Los campos {@code idCita} y {@code sessionDate} son obligatorios.
 * Los identificadores de paciente y psicólogo se deducen automáticamente
 * de la cita vinculada en el servicio.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionRequestDTO {

    /** Identificador de la cita de la que deriva esta sesión. */
    @NotNull
    private Long idCita;

    /** Fecha y hora en que se realizó la sesión. */
    @NotNull
    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. Opcional; valor por defecto 0. */
    private Integer durationMinutes;

    /** Notas clínicas del psicólogo durante la sesión. */
    private String notas;

    /** Recomendaciones emitidas por el psicólogo al finalizar la sesión. */
    private String recomendaciones;
}
