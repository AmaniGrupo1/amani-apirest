package com.amani.amaniapirest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida con los datos del perfil clínico de un paciente.
 *
 * <p>Incluye el identificador del usuario vinculado, datos demográficos
 * y marcas de tiempo de creación y última actualización.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteResponseDTO {

    /** Identificador único del paciente. */
    private Long idPaciente;

    /** Identificador del usuario del sistema asociado al paciente. */
    private Long idUsuario;

    /** Fecha de nacimiento del paciente. */
    private LocalDate fechaNacimiento;

    /** Género del paciente. */
    private String genero;

    /** Número de teléfono de contacto del paciente. */
    private String telefono;

    /** Fecha y hora de creación del perfil de paciente. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del perfil. */
    private LocalDateTime updatedAt;
}
