package com.amani.amaniapirest.dto.dtoPregunta.requestGeneral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPacienteRequestDTO {
    private Long idPaciente;
    private List<RespuestasRequestDTO> respuestas;
}
