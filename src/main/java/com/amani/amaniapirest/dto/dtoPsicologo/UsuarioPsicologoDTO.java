package com.amani.amaniapirest.dto.dtoPsicologo;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPsicologoDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private RolUsuario rol;
    private Boolean activo;
}
