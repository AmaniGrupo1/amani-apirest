package com.amani.amaniapirest.dto.loginDTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistryRequestDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
}
