package com.amani.amaniapirest.dto.dtoPaciente.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignarPacienteAlPsicologoRequestDTO {
    private Long idPaciente;
    private Long idPsicologo;
}
