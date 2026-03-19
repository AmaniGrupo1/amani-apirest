package com.amani.amaniapirest.dto.loginDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de entrada para el registro de un nuevo usuario paciente desde el formulario público.
 *
 * <p>Recoge los datos mínimos necesarios para crear una cuenta: nombre, apellido,
 * correo electrónico y contraseña. El rol se asigna automáticamente como
 * {@code PACIENTE} en el servicio de registro.</p>
 */
@Data
@AllArgsConstructor
public class RegistryRequestDTO {

    /** Nombre de pila del nuevo usuario. */
    private String nombre;

    /** Apellido del nuevo usuario. */
    private String apellido;

    /** Correo electrónico; debe ser único en el sistema. */
    private String email;

    /** Contraseña en texto plano; será hasheada con BCrypt antes de persistir. */
    private String password;
}
