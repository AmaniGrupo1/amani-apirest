package com.amani.amaniapirest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear o actualizar un usuario.
 *
 * <p>Contiene los datos obligatorios de registro: nombre, apellido, email,
 * contraseña (mínimo 6 caracteres) y rol funcional. El campo {@code activo}
 * es opcional; si no se envía, el servicio lo establece a {@code true}.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    /** Nombre de pila del usuario. No puede estar vacío. */
    @NotBlank
    private String nombre;

    /** Apellido del usuario. No puede estar vacío. */
    @NotBlank
    private String apellido;

    /** Correo electrónico del usuario. Debe tener formato válido. */
    @Email
    @NotBlank
    private String email;

    /** Contraseña en texto plano. Será hasheada con BCrypt antes de persistir. */
    @NotBlank
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    /** Rol funcional del usuario (admin, psicólogo, paciente). */
    @NotBlank
    private String rol;

    /** Indica si la cuenta está activa. Por defecto {@code true} si no se especifica. */
    private Boolean activo;
}
