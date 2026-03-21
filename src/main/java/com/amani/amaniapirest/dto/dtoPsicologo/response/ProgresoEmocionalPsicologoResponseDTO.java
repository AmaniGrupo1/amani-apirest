package com.amani.amaniapirest.dto.dtoPsicologo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte el progreso emocional de un paciente.
 *
 * <p>Expone la fecha del registro y los niveles de estrés, ansiedad y ánimo
 * en una escala del 1 al 10.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProgresoEmocionalPsicologoResponse", description = "Progreso emocional — vista psicólogo")
public class ProgresoEmocionalPsicologoResponseDTO {

    /** Fecha del registro del progreso emocional. */
    @Schema(description = "Fecha del registro", example = "2026-03-20")

    private LocalDate fecha;

    /** Nivel de estrés del paciente (escala 1-10). */
    @Schema(description = "Nivel de estrés (1-10)", example = "5")

    private Integer nivelEstres;

    /** Nivel de ansiedad del paciente (escala 1-10). */
    @Schema(description = "Nivel de ansiedad (1-10)", example = "4")

    private Integer nivelAnsiedad;

    /** Nivel de ánimo general del paciente (escala 1-10). */
    @Schema(description = "Nivel de ánimo (1-10)", example = "7")

    private Integer nivelAnimo;
}
