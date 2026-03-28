package com.amani.amaniapirest.dto.dtoPregunta;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoTestResponseDTO {

    private Long idPaciente;

    private Integer puntuacionTotal;

    private String nivel;

}
