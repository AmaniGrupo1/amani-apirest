package com.amani.amaniapirest.dto.dtoAdmin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de entrada para que el administrador cree o actualice el perfil de un {@code Paciente}.
 *
 * <p>Requiere el identificador del {@code Usuario} ya existente en el sistema
 * al que se vinculará el perfil clínico del paciente.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteAdminRequestDTO {

    /**
     * Identificador del {@code Usuario} al que se vinculará este perfil de paciente.
     * <p>El usuario debe existir previamente en el sistema.</p>
     */
    private Long idUsuario;

    /** Fecha de nacimiento del paciente. */
    private LocalDate fechaNacimiento;

    /** Género del paciente (p. ej. "masculino", "femenino", "no binario"). */
    private String genero;

    /** Número de teléfono de contacto del paciente. */
    private String telefono;
}

