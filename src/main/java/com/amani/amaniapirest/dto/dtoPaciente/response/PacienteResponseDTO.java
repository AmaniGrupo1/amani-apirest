package com.amani.amaniapirest.dto.dtoPaciente.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "PacienteResponse", description = "Perfil clínico de un paciente")
public class PacienteResponseDTO {

    /** Fecha de nacimiento del paciente. */
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")

    private LocalDate fechaNacimiento;

    /** Género del paciente. */
    @Schema(description = "Género del paciente", example = "femenino")

    private String genero;

    /** Número de teléfono de contacto del paciente. */
    @Schema(description = "Teléfono de contacto", example = "+34612345678")

    private String telefono;

    /** Fecha y hora de creación del perfil de paciente. */
    @Schema(description = "Fecha de creación del perfil", example = "2026-01-15T08:00:00")

    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del perfil. */
    @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

    private LocalDateTime updatedAt;
}
