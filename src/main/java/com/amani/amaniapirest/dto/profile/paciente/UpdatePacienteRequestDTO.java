package com.amani.amaniapirest.dto.profile.paciente;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePacienteRequestDTO {

    private String telefono;
    private String genero;
    private LocalDate fechaNacimiento;

    private UsuarioUpdateDTO usuario;
}
