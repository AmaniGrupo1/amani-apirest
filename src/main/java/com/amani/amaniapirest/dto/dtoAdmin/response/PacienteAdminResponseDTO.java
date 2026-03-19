package com.amani.amaniapirest.dto.dtoAdmin.response;

import com.amani.amaniapirest.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteAdminResponseDTO {
    private Long idPaciente;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean activo;
}

