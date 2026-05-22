package com.amani.amaniapirest.dto.profile.paciente;

import io.swagger.v3.oas.annotations.media.Schema;

import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para PacienteDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos PacienteDTO")
public class PacienteDTO {
    private Long idPaciente;
    // Datos paciente
    private String telefono;
    private String genero;
    private LocalDate fechaNacimiento;
    private UsuarioDTO usuario;
}
