package com.amani.amaniapirest.dto.dtoAdmin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para que el administrador cree o actualice el perfil de un {@code Psicologo}.
 *
 * <p>Requiere el identificador del {@code Usuario} ya existente en el sistema
 * al que se vinculará el perfil profesional del psicólogo.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoAdminRequestDTO {

    /**
     * Identificador del {@code Usuario} al que se vinculará este perfil de psicólogo.
     * <p>El usuario debe existir previamente en el sistema con rol {@code psicologo}.</p>
     */
    private Long idUsuario;

    /** Especialidad o área de enfoque terapéutico (p. ej. "ansiedad", "trauma"). */
    private String especialidad;

    /** Años de experiencia profesional. */
    private Integer experiencia;

    /** Descripción breve del psicólogo y su enfoque terapéutico. */
    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    private String licencia;
}

