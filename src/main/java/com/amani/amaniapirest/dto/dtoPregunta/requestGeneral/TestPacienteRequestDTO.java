package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de entrada que agrupa las respuestas de un paciente al test inicial.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TestPacienteRequest", description = "Conjunto de respuestas al test inicial")
public class TestPacienteRequestDTO {
    /** Identificador del paciente que responde. */
    @Schema(description = "Identificador del paciente", example = "1")

    private Long idPaciente;
    /** Lista de respuestas del paciente. */
    @Schema(description = "Lista de respuestas del paciente")

    private List<RespuestasRequestDTO> respuestas;
}
