package com.amani.amaniapirest.dto.dtoPregunta.paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el paciente consulte una pregunta del cuestionario de evaluación inicial.
 *
 * <p>Incluye el enunciado, el tipo de pregunta y las opciones de respuesta disponibles.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "PreguntaPacienteResponse", description = "Pregunta del test inicial — vista paciente")
public class PreguntaPacienteResponseDTO {

    /** Enunciado de la pregunta. */
    @Schema(description = "Enunciado de la pregunta", example = "¿Cómo te sientes hoy?")

    private String texto;

    /** Tipo de pregunta (p.ej. "abierta", "opciones", "escala"). */
    @Schema(description = "Tipo de pregunta", example = "opciones")

    private String tipo;

    /** Lista de opciones de respuesta disponibles para la pregunta. */
    @Schema(description = "Opciones de respuesta disponibles")

    private List<String> opciones;
}
