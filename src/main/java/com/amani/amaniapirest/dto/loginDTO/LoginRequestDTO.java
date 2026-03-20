package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO de entrada para la autenticación de un usuario en el sistema.
 *
 * <p>Contiene las credenciales necesarias para obtener un token JWT: el correo
 * electrónico registrado y la contraseña en texto plano.</p>
 */
@Data
@Schema(name = "LoginRequest", description = "Credenciales de autenticación")
public class LoginRequestDTO {

    /** Correo electrónico del usuario registrado. */
    @Schema(description = "Correo electrónico del usuario registrado", example = "usuario@amani.com")
    private String email;

    /** Contraseña en texto plano; se compara contra el hash BCrypt almacenado. */
    @Schema(description = "Contraseña en texto plano", example = "miPassword123")
    private String password;
}
