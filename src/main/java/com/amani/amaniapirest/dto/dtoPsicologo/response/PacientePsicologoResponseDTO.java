package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte los datos básicos de un {@code Paciente}.
 *
 * <p>Muestra únicamente la información demográfica y de contacto del paciente,
 * sin exponer datos de identidad completos ni historial clínico.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PacientePsicologoResponse", description = "Datos básicos de un paciente — vista psicólogo")
public class PacientePsicologoResponseDTO {

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombre;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellido;

    /** Fecha de nacimiento del paciente. */
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")

    private LocalDate fechaNacimiento;

    /** Género del paciente. */
    @Schema(description = "Género", example = "femenino")

    private String genero;

    /** Número de teléfono del paciente. */
    @Schema(description = "Teléfono", example = "+34612345678")

    private String telefono;
}

