package com.amani.amaniapirest.dto.profile.paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {
    private PacienteDTO paciente;
    private String token;
}
