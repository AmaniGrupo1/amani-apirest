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

    /** Correo electrónico del usuario registrado. */
    private String email;

    /** Contraseña en texto plano; se compara contra el hash BCrypt almacenado. */
    private String password;
}
