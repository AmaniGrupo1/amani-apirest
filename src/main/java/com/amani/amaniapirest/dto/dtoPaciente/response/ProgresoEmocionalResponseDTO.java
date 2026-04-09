package com.amani.amaniapirest.dto.dtoPaciente.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida con los datos del progreso emocional de un paciente.
 *
 * <p>Incluye los niveles de estrés, ansiedad y ánimo junto con la fecha
 * del registro y la marca de tiempo de creación.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProgresoEmocionalResponse", description = "Progreso emocional de un paciente")
public class ProgresoEmocionalResponseDTO {

    /** Fecha en que se registró el progreso emocional. */
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

    /** Fecha y hora de creación del registro. */
    @Schema(description = "Fecha de creación del registro", example = "2026-03-20T10:30:00")

    private LocalDateTime createdAt;
}
