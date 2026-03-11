package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con los datos del perfil profesional de un psicólogo.
 *
 * <p>Incluye el identificador del usuario vinculado, datos profesionales
 * y marcas de tiempo de creación y última actualización.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoResponseDTO {

    /** Especialidad o área de enfoque terapéutico del psicólogo. */
    private String especialidad;

    /** Años de experiencia profesional del psicólogo. */
    private Integer experiencia;

    /** Descripción del psicólogo y su metodología de trabajo. */
    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    private String licencia;

    /** Fecha y hora de creación del perfil de psicólogo. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la ultima actualización del perfil. */
    private LocalDateTime updatedAt;
}
