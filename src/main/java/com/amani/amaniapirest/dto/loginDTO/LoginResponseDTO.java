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

    /** Identificador único del usuario autenticado. */
    private Long idUsuario;

    /** Nombre de pila del usuario autenticado. */
    private String nombre;

    /** Rol funcional del usuario (admin, psicologo, paciente). */
    private String rol;
}