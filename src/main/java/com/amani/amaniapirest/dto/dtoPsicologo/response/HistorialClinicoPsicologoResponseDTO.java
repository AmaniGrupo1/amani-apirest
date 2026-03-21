package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte el historial clínico de sus pacientes.
 *
 * <p>Expone los registros clínicos que el psicólogo puede visualizar: título,
 * diagnóstico, tratamiento, observaciones y fecha de creación del registro.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "HistorialClinicoPsicologoResponse", description = "Historial clínico — vista psicólogo")
public class HistorialClinicoPsicologoResponseDTO {

    /** Título o encabezado del registro clínico. */
    @Schema(description = "Título del registro", example = "Evaluación inicial")

    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    @Schema(description = "Diagnóstico", example = "Ansiedad generalizada")

    private String diagnostico;

    /** Tratamiento prescrito o aplicado. */
    @Schema(description = "Tratamiento", example = "TCC semanal")

    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    @Schema(description = "Observaciones", example = "Evolución positiva")

    private String observaciones;

    /** Fecha y hora de creación del registro. */
    @Schema(description = "Fecha de creación", example = "2026-02-10T09:00:00")

    private LocalDateTime creadoEn;
}

