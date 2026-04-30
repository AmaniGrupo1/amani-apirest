package com.amani.amaniapirest.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDTO {
    private String nombre;
    private String apellido;
    private String email;
}
