package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Pattern(
            regexp = "^[a-z][a-z0-9._%+-]*@[a-z0-9.-]+\\.[a-z]{2,}$",
            message = "El email debe empezar en minúscula"
    )
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Debe contener letras y números"
    )
    private String password;
}
