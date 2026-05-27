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
@Schema(description = "Objeto de transferencia de datos LoginResponseDTO")
public class LoginResponseDTO {
    private Long idUsuario;
    private String nombre;
    private String rol;
    private String token;
    private Long idPsicologo;  // Solo se llena si el usuario es un psicólogo
    private Long idPaciente;    // Solo se llena si el usuario es un paciente
    private String idioma;
    private Boolean tema;
}