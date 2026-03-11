package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre un {@code Paciente}.
 *
 * <p>Combina datos del perfil clínico del paciente con los datos de identidad
 * del {@code Usuario} asociado, permitiendo al administrador ver la información
 * completa sin exponer la contraseña.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteAdminResponseDTO {

    /** Identificador único del paciente. */
    private Long idPaciente;

    /** Identificador del usuario vinculado al paciente. */
    private Long idUsuario;

    /** Nombre de pila del usuario vinculado. */
    private String nombreUsuario;

    /** Apellido del usuario vinculado. */
    private String apellidoUsuario;

    /** Correo electrónico del usuario vinculado. */
    private String emailUsuario;

    /** Fecha de nacimiento del paciente. */
    private LocalDate fechaNacimiento;

    /** Género del paciente. */
    private String genero;

    /** Número de teléfono del paciente. */
    private String telefono;

    /** Fecha y hora de creación del perfil. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del perfil. */
    private LocalDateTime updatedAt;
}

