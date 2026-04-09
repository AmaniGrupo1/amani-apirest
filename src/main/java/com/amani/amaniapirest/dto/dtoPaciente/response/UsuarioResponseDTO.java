package com.amani.amaniapirest.dto.dtoPaciente.response;

import com.amani.amaniapirest.enums.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con la información pública de un usuario.
 *
 * <p>Expone los datos del usuario sin incluir la contraseña ni
 * información sensible de seguridad.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UsuarioResponse", description = "Información pública de un usuario")
public class UsuarioResponseDTO {
    /** Nombre de pila del usuario. */
    @Schema(description = "Nombre de pila", example = "Carlos")

    private String nombre;

    /** Apellido del usuario. */
    @Schema(description = "Apellido", example = "López")

    private String apellido;

    /** Correo electrónico del usuario. */
    @Schema(description = "Correo electrónico", example = "carlos@amani.com")

    private String email;

    /** Nombre del rol funcional asignado al usuario. */
    @Schema(description = "Rol funcional del usuario", example = "paciente")

    private RolUsuario rol;

    /** Indica si la cuenta del usuario está activa. */
    @Schema(description = "Indica si la cuenta está activa", example = "true")

    private Boolean activo;

    /** Fecha y hora de registro del usuario en el sistema. */
    @Schema(description = "Fecha de registro", example = "2026-01-15T08:00:00")

    private LocalDateTime fechaRegistro;
}
