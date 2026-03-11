package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de entrada para registrar o actualizar el progreso emocional de un paciente.
 *
 * <p>El campo {@code idPaciente} es obligatorio. Los niveles de estrés,
 * ansiedad y ánimo deben estar en el rango 1-10.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgresoEmocionalRequestDTO {

    /** Identificador del paciente al que pertenece el registro de progreso. */
    @NotNull
    private Long idPaciente;

    /** Fecha del registro. Si es nula, el servicio usa la fecha actual. */
    private LocalDate fecha;

    /** Nivel de estrés del paciente (escala 1-10). */
    @Min(1) @Max(10)
    private Integer nivelEstres;

    /** Nivel de ansiedad del paciente (escala 1-10). */
    @Min(1) @Max(10)
    private Integer nivelAnsiedad;

    /** Nivel de ánimo general del paciente (escala 1-10). */
    @Min(1) @Max(10)
    private Integer nivelAnimo;
}
