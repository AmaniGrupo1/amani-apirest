package com.amani.amaniapirest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de entrada para registrar o modificar el perfil de un paciente.
 *
 * <p>Los campos {@code idUsuario} y {@code fechaNacimiento} son obligatorios.
 * El género y el teléfono son opcionales.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteRequestDTO {

    /** Identificador del usuario del sistema al que se vincula el paciente. */
    @NotNull
    private Long idUsuario;

    /** Fecha de nacimiento del paciente. */
    @NotNull
    private LocalDate fechaNacimiento;

    /** Género del paciente (p.ej. "masculino", "femenino", "no binario"). */
    private String genero;

    /** Número de teléfono de contacto del paciente. */
    private String telefono;
}
