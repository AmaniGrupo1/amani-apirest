package com.amani.amaniapirest.dto.dtoPregunta.psicologo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con los datos de una respuesta de paciente al test inicial,
 * visto desde la perspectiva del psicólogo.
 *
 * <p>Incluye el nombre del paciente, el texto de la pregunta, la respuesta
 * abierta (si aplica), la opción seleccionada y la fecha de creación.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(
        name = "RespuestaPacientePsicologo",
        description = "Respuesta de un paciente al test inicial — vista psicólogo"
)
public class RespuestaPacientePsicologoResponseDTO {

    /** Nombre completo del paciente que respondió. */
    @Schema(description = "Nombre del paciente que respondió el test", example = "Laura")
    private String nombrePaciente;

    /** Texto de la pregunta. */
    @Schema(description = "Texto de la pregunta del test inicial", example = "¿Cómo describirías tu estado de ánimo habitual?")
    private String pregunta;

    /** Texto libre de la respuesta (si aplica). */
    @Schema(description = "Respuesta abierta del paciente (null si solo eligió opción)", example = "Me siento bien la mayor parte del tiempo")
    private String respuesta;

    /** Texto de la opción seleccionada (si aplica). */
    @Schema(description = "Opción seleccionada por el paciente (null si fue respuesta abierta)", example = "Siempre")
    private String opcion;

    /** Fecha y hora en que se registró la respuesta. */
    @Schema(description = "Fecha y hora en que se registró la respuesta", example = "2026-03-20T10:30:00")
    private LocalDateTime creadoEn;
}
