package com.amani.amaniapirest.dto.dtoPsicologo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta para que el psicólogo consulte el progreso emocional de un paciente.
 *
 * <p>Expone la fecha del registro y los niveles de estrés, ansiedad y ánimo
 * en una escala del 1 al 10.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgresoEmocionalPsicologoResponseDTO {

    /** Fecha del registro del progreso emocional. */
    private LocalDate fecha;

    /** Nivel de estrés del paciente (escala 1-10). */
    private Integer nivelEstres;

    /** Nivel de ansiedad del paciente (escala 1-10). */
    private Integer nivelAnsiedad;

    /** Nivel de ánimo general del paciente (escala 1-10). */
    private Integer nivelAnimo;
}
