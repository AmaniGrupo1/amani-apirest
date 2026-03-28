package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de entrada para crear o actualizar una pregunta del cuestionario.
 *
 * <p>El tipo determina cómo se muestra la pregunta al paciente; si el tipo es
 * de opción múltiple, la lista {@code opciones} debe contener al menos dos elementos.</p>
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
