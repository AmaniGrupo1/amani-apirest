package com.amani.amaniapirest.dto.dtoPregunta.psicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte las respuestas del cuestionario de un paciente.
 *
 * <p>Agrupa el nombre del paciente, el enunciado de la pregunta, la respuesta
 * en texto libre, la opción seleccionada (si aplica) y la fecha de registro.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RespuestaPacientePsicologoResponseDTO {

    /** Nombre completo del paciente que respondió el cuestionario. */
    private String nombrePaciente;

    /** Enunciado de la pregunta respondida. */
    private String pregunta;

    /** Respuesta en texto libre del paciente, si la pregunta es de tipo abierto. */
    private String respuesta;

    /** Opción seleccionada por el paciente, si la pregunta es de tipo opción múltiple. */
    private String opcion;

    /** Fecha y hora en que el paciente registró la respuesta. */
    private LocalDateTime creadoEn;
}
