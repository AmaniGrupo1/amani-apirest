package com.amani.amaniapirest.dto.dtoPaciente.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con la información de una sesión terapéutica.
 *
 * <p>Incluye el identificador de la cita asociada, la fecha de realización,
 * la duración, las notas clínicas y las recomendaciones del psicólogo.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SesionResponse", description = "Información de una sesión terapéutica — vista paciente")
public class SesionResponseDTO {


    /** Fecha y hora en que se realizó la sesión. */
    @Schema(description = "Fecha y hora de la sesión", example = "2026-04-01T11:00:00")

    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    @Schema(description = "Duración efectiva en minutos", example = "50")

    private Integer durationMinutes;

    /** Notas clínicas registradas por el psicólogo durante la sesión. */
    @Schema(description = "Notas clínicas", example = "Paciente muestra mejora")

    private String notas;

    /** Recomendaciones emitidas por el psicólogo al finalizar la sesión. */
    @Schema(description = "Recomendaciones del psicólogo", example = "Continuar ejercicios")

    private String recomendaciones;
}
