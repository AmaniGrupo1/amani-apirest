package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre el progreso emocional de un paciente.
 *
 * <p>Incluye el nombre del paciente, la fecha del registro y los niveles de
 * estrés, ansiedad y ánimo en escala 1-10, junto con la marca de tiempo de creación.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgresoEmocionalAdminResponseDTO {

    /** Nombre completo del paciente al que pertenece el registro. */
    private String nombrePaciente;
    /** Fecha del registro del progreso emocional. */
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