package com.amani.amaniapirest.dto.loginDTO;

import lombok.Data;

/**
 * DTO de entrada para la autenticación de un usuario en el sistema.
 *
 * <p>Contiene las credenciales necesarias para obtener un token JWT: el correo
 * electrónico registrado y la contraseña en texto plano.</p>
 */
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
