package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con los datos de un registro del historial clínico.
 *
 * <p>Incluye el identificador del paciente, título, diagnóstico,
 * tratamiento, observaciones y fecha de creación.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoResponseDTO {

    /** Título o encabezado del registro clínico. */
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    private String diagnostico;

    /** Descripción del tratamiento prescrito o aplicado. */
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    private String observaciones;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime creadoEn;
}
