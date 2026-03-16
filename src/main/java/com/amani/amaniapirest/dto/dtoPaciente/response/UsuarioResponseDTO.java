package com.amani.amaniapirest.dto.dtoPaciente.response;
import com.amani.amaniapirest.enums.RolUsuario;
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
public class UsuarioResponseDTO {

    /** Nombre de pila del usuario. */
    private String nombre;

    /** Apellido del usuario. */
    private String apellido;

    /** Correo electrónico del usuario. */
    private String email;

    /** Nombre del rol funcional asignado al usuario. */
    private RolUsuario rol;

    /** Indica si la cuenta del usuario está activa. */
    private Boolean activo;

    /** Fecha y hora de registro del usuario en el sistema. */
    private LocalDateTime fechaRegistro;
}
