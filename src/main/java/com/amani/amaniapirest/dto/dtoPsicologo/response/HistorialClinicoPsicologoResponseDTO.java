package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte el historial clínico de sus pacientes.
 *
 * <p>Expone los registros clínicos que el psicólogo puede visualizar: título,
 * diagnóstico, tratamiento, observaciones y fecha de creación del registro.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoPsicologoResponseDTO {

    /** Título o encabezado del registro clínico. */
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    private String diagnostico;

    /** Tratamiento prescrito o aplicado. */
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    private String observaciones;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime creadoEn;
}

