package com.amani.amaniapirest.dto.dtoAdmin.request;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para que el administrador cree o actualice un {@code Usuario}.
 *
 * <p>Incluye todos los campos editables por el administrador: datos personales,
 * credenciales (la contraseña se recibirá en texto plano y será hasheada en el
 * servicio), rol asignado y estado de la cuenta.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAdminRequestDTO {

    /** Nombre de pila del usuario. */
    private String nombre;

    /** Apellido del usuario. */
    private String apellido;

    /** Correo electrónico único utilizado para el inicio de sesión. */
    private String email;

    /**
     * Contraseña en texto plano.
     * <p>El servicio aplicará el hash BCrypt antes de persistirla.</p>
     */
    private String password;

    /** Rol funcional que se asignará al usuario. */
    private RolUsuario rol;

    /** Indica si la cuenta debe estar activa al crearse o actualizarse. */
    private Boolean activo;
}

