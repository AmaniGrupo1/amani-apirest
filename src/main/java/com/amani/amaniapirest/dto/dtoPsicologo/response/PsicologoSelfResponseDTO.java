package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para que el psicólogo consulte su propio perfil profesional.
 *
 * <p>Muestra los datos del perfil del psicólogo autenticado: especialidad,
 * experiencia, descripción y número de licencia.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoSelfResponseDTO {

    private Long idPsicologo;

    private String nombre;

    private String apellido;

    private String especialidad;

    /** Años de experiencia profesional. */
    private Integer experiencia;

    /** Descripción del psicólogo y su enfoque. */
    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    private String licencia;
}

