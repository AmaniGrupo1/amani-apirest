package com.amani.amaniapirest.dto.loginDTO;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Long idUsuario;
    private String nombre;
    private String rol;
}