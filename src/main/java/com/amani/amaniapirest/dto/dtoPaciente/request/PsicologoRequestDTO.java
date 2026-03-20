package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para registrar o actualizar el perfil de un psicólogo.
 *
 * <p>Los campos {@code idUsuario} y {@code especialidad} son obligatorios.
 * La experiencia, descripción y licencia son opcionales.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PsicologoRequest", description = "Datos para registrar o actualizar el perfil de un psicólogo")
public class PsicologoRequestDTO {

    /** Identificador del usuario del sistema al que se vincula el psicólogo. */
    @NotNull
    @Schema(description = "Identificador del usuario vinculado al psicólogo", example = "3")
    private Long idUsuario;

    /** Especialidad o área de enfoque terapéutico del psicólogo. */
    @NotBlank
    @Schema(description = "Especialidad o área de enfoque terapéutico", example = "Psicología clínica")
    private String especialidad;

    /** Años de experiencia profesional. Opcional; valor por defecto 0. */
    @Schema(description = "Años de experiencia profesional", example = "10")
    private Integer experiencia;

    /** Descripción breve del psicólogo y su metodología de trabajo. */
    @Schema(description = "Descripción del psicólogo y su metodología", example = "Especialista en terapia cognitivo-conductual")
    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    @Schema(description = "Número de licencia o colegiación profesional", example = "COL-12345")
    private String licencia;
}
