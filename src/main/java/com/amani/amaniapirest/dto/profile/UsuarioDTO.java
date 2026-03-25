package com.amani.amaniapirest.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private Long id;

    private String nombre;

    private String apellido;

    private String email;

    private String rol;

    private Boolean activo;
}
