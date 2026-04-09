package com.amani.amaniapirest.dto.dtoPaciente.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "HistorialClinicoResponse", description = "Registro del historial clínico — vista paciente")
public class HistorialClinicoResponseDTO {

    /** Título o encabezado del registro clínico. */
    @Schema(description = "Título del registro", example = "Evaluación inicial")

    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    @Schema(description = "Diagnóstico", example = "Ansiedad generalizada")

    private String diagnostico;

    /** Descripción del tratamiento prescrito o aplicado. */
    @Schema(description = "Tratamiento prescrito", example = "Terapia cognitivo-conductual")

    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    @Schema(description = "Observaciones del profesional", example = "Evolución positiva")

    private String observaciones;

    /** Fecha y hora de creación del registro. */
    @Schema(description = "Fecha de creación", example = "2026-02-10T09:00:00")

    private LocalDateTime creadoEn;
}
