package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte su propio perfil profesional.
 *
 * <p>Muestra los datos del perfil del psicólogo autenticado: especialidad,
 * experiencia, descripción y número de licencia.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PsicologoSelfResponse", description = "Perfil profesional propio del psicólogo")
public class PsicologoSelfResponseDTO {

    /** Identificador único del psicólogo. */
    @Schema(description = "Identificador del psicólogo", example = "1")

    private Long idPsicologo;

    /** Especialidad o área de enfoque terapéutico. */
    @Schema(description = "Especialidad", example = "Psicología clínica")

    private String especialidad;

    /** Años de experiencia profesional. */
    @Schema(description = "Años de experiencia", example = "10")

    private Integer experiencia;

    /** Descripción del psicólogo y su enfoque. */
    @Schema(description = "Descripción profesional", example = "Especialista en TCC")

    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    @Schema(description = "Número de licencia", example = "COL-12345")

    private String licencia;
}

