package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "RegistryRequest", description = "Datos de registro de un nuevo usuario (admin/psicólogo)")
public class RegistryRequestDTO {

    @Schema(description = "Nombre de pila del nuevo usuario", example = "María")
    /** Nombre de pila del nuevo usuario. */
    private String nombre;

    @Schema(description = "Apellido del nuevo usuario", example = "García")
    /** Apellido del nuevo usuario. */
    private String apellido;

    @Schema(description = "Correo electrónico; debe ser único en el sistema", example = "maria@amani.com")
    /** Correo electrónico; debe ser único en el sistema. */
    private String email;

    @Schema(description = "Contraseña en texto plano; será hasheada con BCrypt", example = "securePass456")
    /** Contraseña en texto plano; será hasheada con BCrypt antes de persistir. */
    private String password;
}
