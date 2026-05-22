package com.amani.amaniapirest.dto.roles;

import io.swagger.v3.oas.annotations.media.Schema;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para CambiarRolResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos CambiarRolResponseDTO")
public class CambiarRolResponseDTO {

    private Long idUsuario;

    private String nombre;

    private String email;

    private RolUsuario rolAnterior;

    private RolUsuario nuevoRol;

    private String mensaje;

    // NUEVO TOKEN
    private String token;

    // NUEVOS IDS
    private Long idPsicologo;

    private Long idPaciente;

    // AJUSTES
    private String idioma;

    private Boolean tema;
}
