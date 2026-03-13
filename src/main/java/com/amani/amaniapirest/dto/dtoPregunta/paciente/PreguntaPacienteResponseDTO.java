package com.amani.amaniapirest.dto.dtoPregunta.paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreguntaPacienteResponseDTO {
    private String texto;
    private String tipo;
    private List<OpcionPacienteResponseDTO> opciones;
}
