package com.amani.amaniapirest.dto.dtoPsicologo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para que el psicólogo actualice su propio perfil profesional.
 *
 * <p>Solo permite modificar los datos del perfil profesional; los datos de
 * identidad como email y contraseña se gestionan por separado.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoSelfRequestDTO {

    /** Nueva especialidad o área de enfoque terapéutico. */
    private String especialidad;

    /** Años de experiencia profesional actualizados. */
    private Integer experiencia;

    /** Nueva descripción del psicólogo y su enfoque. */
    private String descripcion;

    /** Número de licencia o colegiación profesional actualizado. */
    private String licencia;
}

