package com.amani.amaniapirest.dto.loginDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta devuelto tras una autenticación exitosa.
 *
 * <p>Proporciona al cliente el identificador del usuario, su nombre para mostrar
 * y su rol, necesarios para configurar la sesión en el frontend.</p>
 */
@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String rol;
    private String token;
}