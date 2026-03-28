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
@Schema(name = "LoginRequest", description = "Credenciales para iniciar sesión y obtener un token JWT")
public class LoginRequestDTO {

    /** Correo electrónico registrado del usuario. */
    @Schema(description = "Correo electrónico registrado del usuario", example = "paciente@amani.com")
    private String email;

    /** Contraseña en texto plano del usuario. */
    @Schema(description = "Contraseña en texto plano", example = "miPassword123")
    private String password;
}
