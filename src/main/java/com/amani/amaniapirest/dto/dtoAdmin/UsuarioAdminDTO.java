package com.amani.amaniapirest.dto.dtoAdmin;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAdminDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaBaja;
}