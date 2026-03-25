package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta devuelto tras una autenticación exitosa.
 *
 * <p>Proporciona al cliente los datos básicos del usuario autenticado y
 * el token JWT necesarios para configurar la sesión en el frontend.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LoginResponse", description = "Respuesta tras autenticación exitosa con token JWT")
public class LoginResponseDTO {

    /** Datos del usuario autenticado. */
    @Schema(description = "Datos del usuario autenticado")
    private UsuarioDTO usuario;

    /** Token JWT generado para las peticiones autenticadas. */
    @Schema(description = "Token JWT para autorización en cabecera Bearer", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}
