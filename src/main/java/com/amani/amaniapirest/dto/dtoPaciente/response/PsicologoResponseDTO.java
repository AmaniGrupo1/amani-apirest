package com.amani.amaniapirest.dto.dtoPaciente.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida con los datos del perfil profesional de un psicólogo.
 *
 * <p>Incluye el identificador del usuario vinculado, datos profesionales
 * y marcas de tiempo de creación y última actualización.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PsicologoResponse", description = "Perfil profesional de un psicólogo — vista paciente")
public class PsicologoResponseDTO {
    /** Especialidad o área de enfoque terapéutico del psicólogo. */
    @Schema(description = "Especialidad terapéutica", example = "Psicología clínica")

    private String especialidad;

    /** Años de experiencia profesional del psicólogo. */
    @Schema(description = "Años de experiencia", example = "10")

    private Integer experiencia;

    /** Descripción del psicólogo y su metodología de trabajo. */
    @Schema(description = "Descripción profesional", example = "Especialista en TCC")

    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    @Schema(description = "Número de licencia", example = "COL-12345")

    private String licencia;

    /** Fecha y hora de creación del perfil de psicólogo. */
    @Schema(description = "Fecha de creación del perfil", example = "2026-01-01T08:00:00")

    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del perfil. */
    @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

    private LocalDateTime updatedAt;
}
