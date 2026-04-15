package com.amani.amaniapirest.dto.dtoPregunta;


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
public class ResultadoTestResponseDTO {

    private Long idPaciente;

    private Integer puntuacionTotal;

    private String nivel;

}
