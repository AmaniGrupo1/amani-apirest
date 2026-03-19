package com.amani.amaniapirest.dto.dtoPregunta.paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para que el paciente consulte una pregunta del cuestionario de evaluación inicial.
 *
 * <p>Incluye el enunciado, el tipo de pregunta y las opciones de respuesta disponibles.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreguntaPacienteResponseDTO {

    /** Enunciado de la pregunta. */
    private String texto;

    /** Tipo de pregunta (p.ej. "abierta", "opciones", "escala"). */
    private String tipo;

    /** Lista de opciones de respuesta disponibles para la pregunta. */
    private List<String> opciones;
}
