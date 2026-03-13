package com.amani.amaniapirest.dto.dtoPaciente.response;

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
public class ProgresoEmocionalResponseDTO {

    /** Fecha en que se registró el progreso emocional. */
    private LocalDate fecha;

    /** Nivel de estrés del paciente (escala 1-10). */
    private Integer nivelEstres;

    /** Nivel de ansiedad del paciente (escala 1-10). */
    private Integer nivelAnsiedad;

    /** Nivel de ánimo general del paciente (escala 1-10). */
    private Integer nivelAnimo;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime createdAt;
}
