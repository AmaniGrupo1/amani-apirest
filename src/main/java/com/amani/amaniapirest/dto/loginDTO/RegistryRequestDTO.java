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

    private String nombre;

    private String apellido;

    private String email;

    private String password;
}
