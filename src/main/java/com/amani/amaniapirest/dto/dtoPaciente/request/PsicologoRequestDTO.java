package com.amani.amaniapirest.dto.dtoPaciente.request;

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
public class PsicologoRequestDTO {

    /** Identificador del usuario del sistema al que se vincula el psicólogo. */
    @NotNull
    private Long idUsuario;

    /** Especialidad o área de enfoque terapéutico del psicólogo. */
    @NotBlank
    private String especialidad;

    /** Años de experiencia profesional. Opcional; valor por defecto 0. */
    private Integer experiencia;

    /** Descripción breve del psicólogo y su metodología de trabajo. */
    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    private String licencia;
}
