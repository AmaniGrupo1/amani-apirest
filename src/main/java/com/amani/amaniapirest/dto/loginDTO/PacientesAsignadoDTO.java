package com.amani.amaniapirest.dto.loginDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientesAsignadoDTO {
    private Long idPaciente;        // Id del paciente
    private String nombre;          // Nombre del paciente
    private String apellido;        // Apellido del paciente
    private String email;           // Email del paciente
}
