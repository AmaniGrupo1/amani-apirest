package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta devuelto tras una autenticación exitosa.
 *
 * <p>Proporciona al cliente el identificador del usuario, su nombre para mostrar
 * y su rol, necesarios para configurar la sesión en el frontend.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LoginResponse", description = "Respuesta tras autenticación exitosa con token JWT")
public class LoginResponseDTO {

    /** Identificador único del usuario autenticado. */
    @Schema(description = "Identificador único del usuario", example = "1")
    private Long idUsuario;

    /** Nombre para mostrar del usuario. */
    @Schema(description = "Nombre del usuario", example = "Laura")
    private String nombre;

    /** Rol asignado al usuario (paciente, psicologo, admin). */
    @Schema(description = "Rol del usuario en el sistema", example = "paciente")
    private String rol;

    /** Token JWT generado para las peticiones autenticadas. */
    @Schema(description = "Token JWT para autorización en cabecera Bearer", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}