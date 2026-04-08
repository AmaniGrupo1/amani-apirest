package com.amani.amaniapirest.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteDTO {
    private Long idPaciente;
    // Datos paciente
    private String telefono;
    private String genero;
    private LocalDate fechaNacimiento;
    private UsuarioDTO usuario;
}
