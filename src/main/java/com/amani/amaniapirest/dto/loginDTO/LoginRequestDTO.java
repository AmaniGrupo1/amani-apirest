package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Pattern(
            regexp = "^[a-z][a-z0-9._%+-]*@[a-z0-9.-]+\\.[a-z]{2,}$",
            message = "El email debe empezar en minúscula"
    )
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Debe contener letras y números"
    )
    private String password;
}
