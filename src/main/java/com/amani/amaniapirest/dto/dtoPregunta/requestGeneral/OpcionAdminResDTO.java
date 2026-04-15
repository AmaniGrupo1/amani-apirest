package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para representar una opción de respuesta en un cuestionario.
 *
 * <p>Contiene la información de una opción de respuesta predefinida que
 * puede ser asociada a una pregunta del cuestionario psicológico.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpcionAdminResDTO {

    private Long id;
    /**
     * Enunciado de la pregunta.
     */
    private String texto;

    /**
     * Tipo de pregunta (p.ej. "abierta", "opciones", "escala").
     */
    private String tipo;

    /**
     * Opciones de respuesta disponibles; requeridas cuando el tipo es "opciones".
     */
    private List<String> opciones;
}
