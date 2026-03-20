package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ProgresoEmocionalRequest", description = "Datos para registrar o actualizar el progreso emocional de un paciente")
public class ProgresoEmocionalRequestDTO {

    /** Identificador del paciente al que pertenece el registro de progreso. */
    @NotNull
    @Schema(description = "Identificador del paciente", example = "1")
    private Long idPaciente;

    /** Fecha del registro. Si es nula, el servicio usa la fecha actual. */
    @Schema(description = "Fecha del registro; si es nula se usa la fecha actual", example = "2026-03-20")
    private LocalDate fecha;

    /** Nivel de estrés del paciente (escala 1-10). */
    @Min(1) @Max(10)
    @Schema(description = "Nivel de estrés (escala 1-10)", example = "5")
    private Integer nivelEstres;

    /** Nivel de ansiedad del paciente (escala 1-10). */
    @Min(1) @Max(10)
    @Schema(description = "Nivel de ansiedad (escala 1-10)", example = "4")
    private Integer nivelAnsiedad;

    /** Nivel de ánimo general del paciente (escala 1-10). */
    @Min(1) @Max(10)
    @Schema(description = "Nivel de ánimo general (escala 1-10)", example = "7")
    private Integer nivelAnimo;
}
