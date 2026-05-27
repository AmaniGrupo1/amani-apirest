package com.amani.amaniapirest.dto.profile.paciente;

import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para UpdatePacienteRequestDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos UpdatePacienteRequestDTO")
public class UpdatePacienteRequestDTO {

    private String telefono;
    private String genero;
    private LocalDate fechaNacimiento;

    private UsuarioUpdateDTO usuario;
}
