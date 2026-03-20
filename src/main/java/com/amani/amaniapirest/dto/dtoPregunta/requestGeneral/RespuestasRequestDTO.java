package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "RespuestasRequest", description = "Respuesta de un paciente a una pregunta del test")
public class RespuestasRequestDTO {

    /** Identificador de la pregunta que se responde. */
    @Schema(description = "Identificador de la pregunta", example = "1")

    private Long idPregunta;

    /** Identificador de la opción seleccionada por el paciente; nulo si la pregunta es abierta. */
    @Schema(description = "Opción seleccionada (null si abierta)", example = "3")

    private Long idOpcion;

    /** Respuesta en texto libre del paciente; nulo si la pregunta es de opción múltiple. */
    @Schema(description = "Respuesta en texto libre (null si opción)", example = "Me siento bien")

    private String texto;
}
