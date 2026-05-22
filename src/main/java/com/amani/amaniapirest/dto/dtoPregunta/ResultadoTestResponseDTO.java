package com.amani.amaniapirest.dto.dtoPregunta;

import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para representar el resultado de un test psicológico.
 *
 * <p>Contiene la puntuación obtenida por el paciente y el nivel de severidad
 * correspondiente según los resultados del test.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos ResultadoTestResponseDTO")
public class ResultadoTestResponseDTO {

    private Long idPaciente;

    private Integer puntuacionTotal;

    private String nivel;

}
