package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta para que el psicólogo consulte los datos básicos de un {@code Paciente}.
 *
 * <p>Muestra únicamente la información demográfica y de contacto del paciente,
 * sin exponer datos de identidad completos ni historial clínico.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientePsicologoResponseDTO {

    /** Identificador único del paciente. */
    private Long idPaciente;

    /** Nombre de pila del paciente. */
    private String nombre;

    /** Apellido del paciente. */
    private String apellido;

    /** Fecha de nacimiento del paciente. */
    private LocalDate fechaNacimiento;

    /** Género del paciente. */
    private String genero;

    /** Número de teléfono del paciente. */
    private String telefono;
}

