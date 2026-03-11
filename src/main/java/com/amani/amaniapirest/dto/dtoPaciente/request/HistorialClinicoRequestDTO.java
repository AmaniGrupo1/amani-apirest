package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear o actualizar un registro del historial clínico.
 *
 * <p>Los campos {@code idPaciente} y {@code titulo} son obligatorios.
 * El diagnóstico, tratamiento y observaciones son opcionales.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoRequestDTO {

    /** Identificador del paciente al que pertenece el registro. */
    @NotNull
    private Long idPaciente;

    /** Título o encabezado del registro clínico. */
    @NotBlank
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    private String diagnostico;

    /** Descripción del tratamiento prescrito o aplicado. */
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    private String observaciones;
}
