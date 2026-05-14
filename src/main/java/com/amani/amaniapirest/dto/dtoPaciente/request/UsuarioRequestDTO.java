package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.enums.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Schema(name = "UsuarioRequest", description = "Datos para crear o actualizar un usuario")
public class UsuarioRequestDTO {

    private Long id;

    /** Nombre de pila del usuario. No puede estar vacío. */
    @NotBlank
    @Schema(description = "Nombre de pila del usuario", example = "Carlos")
    private String nombre;

    @NotBlank
    private String dni;

    /** Apellido del usuario. No puede estar vacío. */
    @NotBlank
    @Schema(description = "Apellido del usuario", example = "López")
    private String apellido;

    /** Correo electrónico del usuario. Debe tener formato válido. */
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

    @Schema(description = "Rol funcional del usuario (admin, psicologo, paciente)", example = "paciente")
    private RolUsuario rol;

    /** Indica si la cuenta está activa. Por defecto {@code true} si no se especifica. */
    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private Boolean activo;
}
