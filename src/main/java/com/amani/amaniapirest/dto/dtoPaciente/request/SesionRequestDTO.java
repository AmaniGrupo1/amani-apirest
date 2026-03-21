package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para crear o modificar una sesión terapéutica.
 *
 * <p>Los campos {@code idCita} y {@code sessionDate} son obligatorios.
 * Los identificadores de paciente y psicólogo se deducen automáticamente
 * de la cita vinculada en el servicio.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SesionRequest", description = "Datos para crear o modificar una sesión terapéutica")
public class SesionRequestDTO {

    /** Identificador de la cita de la que deriva esta sesión. */
    @NotNull
    @Schema(description = "Identificador de la cita de la que deriva esta sesión", example = "3")
    private Long idCita;

    /** Fecha y hora en que se realizó la sesión. */
    @NotNull
    @Schema(description = "Fecha y hora en que se realizó la sesión", example = "2026-04-01T11:00:00")
    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. Opcional; valor por defecto 0. */
    @Schema(description = "Duración efectiva de la sesión en minutos", example = "50")
    private Integer durationMinutes;

    /** Notas clínicas del psicólogo durante la sesión. */
    @Schema(description = "Notas clínicas del psicólogo durante la sesión", example = "Paciente muestra mejora significativa")
    private String notas;

    /** Recomendaciones emitidas por el psicólogo al finalizar la sesión. */
    @Schema(description = "Recomendaciones emitidas por el psicólogo", example = "Continuar con ejercicios de relajación diarios")
    private String recomendaciones;
}
