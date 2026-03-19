package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para registrar las respuestas de un paciente a una pregunta del cuestionario.
 *
 * <p>Si la pregunta es de tipo opción múltiple se rellena {@code idOpcion}; si es
 * abierta se rellena {@code texto}. Ambos campos son opcionales pero al menos uno
 * debe estar presente.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RespuestasRequestDTO {

    /** Identificador de la pregunta que se responde. */
    private Long idPregunta;

    /** Identificador de la opción seleccionada por el paciente; nulo si la pregunta es abierta. */
    private Long idOpcion;

    /** Respuesta en texto libre del paciente; nulo si la pregunta es de opción múltiple. */
    private String texto;
}
