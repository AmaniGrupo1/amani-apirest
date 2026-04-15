package com.amani.amaniapirest.dto.situacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para representar una situación psicosocial.
 *
 * <p>Contiene la información de una situación que puede afectar
 * el estado emocional o mental del paciente, utilizada en el
 * análisis y seguimiento clínico.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SituacionDTO {
    private Long idSituacion;
    private String nombre;
    private String categoria;
    private String descripcion;
}
