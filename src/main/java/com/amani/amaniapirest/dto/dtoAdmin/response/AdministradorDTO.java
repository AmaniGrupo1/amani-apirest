package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdministradorDTO {

    private Long idUsuario;

    private String nombre;

    private String apellido;

    private String email;

    private String rol;

    private Boolean activo;
}