package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgresoEmocionalAdminResponseDTO {

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