package com.amani.amaniapirest.dto.dtoAdmin.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para representar un administrador del sistema.
 *
 * <p>Contiene la información identificativa y de acceso del administrador,
 * incluyendo sus credenciales de usuario y estado de actividad.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos AdministradorDTO")
public class AdministradorDTO {

    private Long idUsuario;

    private String nombre;

    private String apellido;

    private String email;

    private String rol;

    private Boolean activo;
}