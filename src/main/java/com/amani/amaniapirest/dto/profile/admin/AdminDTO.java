package com.amani.amaniapirest.dto.profile.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String fotoPerfilUrl;
}
