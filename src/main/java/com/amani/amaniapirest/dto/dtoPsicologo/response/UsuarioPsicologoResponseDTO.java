package com.amani.amaniapirest.dto.dtoPsicologo.response;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para que el psicólogo consulte sus propios datos de {@code Usuario}.
 *
 * <p>Expone únicamente los campos de identidad necesarios para el perfil del
 * profesional, sin incluir información sensible como la contraseña.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPsicologoResponseDTO {

    /** Identificador único del usuario. */
    private Long idUsuario;

    /** Nombre de pila del psicólogo. */
    private String nombre;

    /** Apellido del psicólogo. */
    private String apellido;

    /** Correo electrónico del psicólogo. */
    private String email;

    /** Rol del usuario en el sistema (siempre {@code psicologo} en este contexto). */
    private RolUsuario rol;

    /** Indica si la cuenta está activa. */
    private Boolean activo;
}

