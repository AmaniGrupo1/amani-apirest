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
@Schema(name = "RegistryRequest", description = "Datos para el registro de un nuevo usuario (admin o psicólogo)")
public class RegistryRequestDTO {

    /** Nombre de pila del nuevo usuario. */
    @Schema(description = "Nombre de pila del nuevo usuario", example = "Carlos")
    private String nombre;

    /** Apellido del nuevo usuario. */
    @Schema(description = "Apellido del nuevo usuario", example = "López")
    private String apellido;

    /** Correo electrónico que se usará como credencial de acceso. */
    @Schema(description = "Correo electrónico del nuevo usuario", example = "carlos@amani.com")
    private String email;

    /** Contraseña en texto plano; se almacenará cifrada con BCrypt. */
    @Schema(description = "Contraseña en texto plano (se cifra con BCrypt)", example = "miPassword123")
    private String password;
}
