package com.amani.amaniapirest.dto.profile.admin;

import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para AdminDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos AdminDTO")
public class AdminDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String fotoPerfilUrl;
}
