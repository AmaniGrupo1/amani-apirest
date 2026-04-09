package com.amani.amaniapirest.dto.dtoAdmin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre un {@code HistorialClinico}.
 *
 * <p>Expone el registro clínico completo incluyendo los datos de identificación
 * del paciente vinculado, el diagnóstico, el tratamiento y las observaciones
 * del profesional.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "HistorialClinicoAdminResponse", description = "Historial clínico — vista administrador")
public class HistorialClinicoAdminResponseDTO {

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

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

