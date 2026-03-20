package com.amani.amaniapirest.dto.dtoPsicologo.response;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte sus propios datos de {@code Usuario}.
 *
 * <p>Expone únicamente los campos de identidad necesarios para el perfil del
 * profesional, sin incluir información sensible como la contraseña.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UsuarioPsicologoResponse", description = "Perfil de usuario — vista psicólogo")
public class UsuarioPsicologoResponseDTO {

    /** Identificador único del usuario. */
    @Schema(description = "Identificador del usuario", example = "3")

    private Long idUsuario;

    /** Nombre de pila del psicólogo. */
    @Schema(description = "Nombre", example = "Ana")

    private String nombre;

    /** Apellido del psicólogo. */
    @Schema(description = "Apellido", example = "García")

    private String apellido;

    /** Correo electrónico del psicólogo. */
    @Schema(description = "Email", example = "ana@amani.com")

    private String email;

    /** Rol del usuario en el sistema (siempre {@code psicologo} en este contexto). */
    @Schema(description = "Rol del usuario", example = "psicologo")

    private String rol;

    /** Indica si la cuenta está activa. */
    @Schema(description = "Si la cuenta está activa", example = "true")

    private Boolean activo;
}

