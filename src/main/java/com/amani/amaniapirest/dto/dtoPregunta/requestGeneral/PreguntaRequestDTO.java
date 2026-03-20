package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de entrada para crear o actualizar una pregunta del cuestionario.
 *
 * <p>El tipo determina cómo se muestra la pregunta al paciente; si el tipo es
 * de opción múltiple, la lista {@code opciones} debe contener al menos dos elementos.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "PreguntaRequest", description = "Datos para crear una pregunta del cuestionario")
public class PreguntaRequestDTO {

    /** Enunciado de la pregunta. */
    @Schema(description = "Enunciado de la pregunta", example = "¿Cómo te sientes hoy?")

    private String texto;

    /** Tipo de pregunta (p.ej. "abierta", "opciones", "escala"). */
    @Schema(description = "Tipo de pregunta (abierta, opciones, escala)", example = "opciones")

    private String tipo;

    /** Opciones de respuesta disponibles; requeridas cuando el tipo es "opciones". */
    @Schema(description = "Opciones de respuesta disponibles", example = "[Bien, Regular, Mal]")

    private List<String> opciones;
}
