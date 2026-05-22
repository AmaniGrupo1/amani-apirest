package com.amani.amaniapirest.dto.situacion;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Objeto de transferencia de datos SituacionDTO")
public class SituacionDTO {
    private Long idSituacion;
    private String nombre;
    private String categoria;
    private String descripcion;
}
