package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "HistorialClinicoRequest", description = "Datos para crear o actualizar un registro del historial clínico")
public class HistorialClinicoRequestDTO {

    /** Identificador del paciente al que pertenece el registro. */
    @NotNull
    @Schema(description = "Identificador del paciente", example = "1")
    private Long idPaciente;

    /** Título o encabezado del registro clínico. */
    @NotBlank
    @Schema(description = "Título o encabezado del registro clínico", example = "Evaluación inicial")
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    @Schema(description = "Diagnóstico emitido por el psicólogo", example = "Trastorno de ansiedad generalizada")
    private String diagnostico;

    /** Descripción del tratamiento prescrito o aplicado. */
    @Schema(description = "Tratamiento prescrito o aplicado", example = "Terapia cognitivo-conductual semanal")
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    @Schema(description = "Observaciones adicionales del profesional", example = "Paciente colaborador, evolución positiva")
    private String observaciones;
}
