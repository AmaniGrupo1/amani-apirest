package com.amani.amaniapirest.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteAdminDTO {
    private Long idPaciente;
    private Long idUsuario;
    private String nombreUsuario;  // del usuario vinculado
    private String apellidoUsuario;
    private String emailUsuario;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}