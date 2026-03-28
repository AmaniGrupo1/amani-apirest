package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la vista de administrador sobre el progreso emocional de un paciente.
 *
 * <p>Incluye el nombre del paciente, la fecha del registro y los niveles de
 * estrés, ansiedad y ánimo en escala 1-10, junto con la marca de tiempo de creación.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProgresoEmocionalAdminResponse", description = "Progreso emocional — vista administrador")
public class ProgresoEmocionalAdminResponseDTO {

    /** Nombre completo del paciente al que pertenece el registro. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;
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

    /** Fecha y hora de creación del registro. */
    @Schema(description = "Fecha de creación", example = "2026-03-20T10:30:00")

    private LocalDateTime createdAt;
}