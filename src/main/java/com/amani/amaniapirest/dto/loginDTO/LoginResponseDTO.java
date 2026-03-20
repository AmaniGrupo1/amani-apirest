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
@Schema(name = "LoginResponse", description = "Respuesta de autenticación exitosa con token JWT")
public class LoginResponseDTO {

    @Schema(description = "Identificador único del usuario autenticado", example = "1")
    private Long idUsuario;

    @Schema(description = "Nombre de pila del usuario autenticado", example = "Carlos")
    private String nombre;

    @Schema(description = "Rol funcional del usuario (admin, psicologo, paciente)", example = "paciente")
    private String rol;

    @Schema(description = "Token JWT para autenticación en peticiones subsiguientes", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}