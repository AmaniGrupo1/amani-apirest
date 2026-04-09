package com.amani.amaniapirest.dto.dtoPsicologo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte las sesiones que ha impartido.
 *
 * <p>Incluye los datos del paciente atendido, la fecha, la duración, las notas
 * clínicas y las recomendaciones registradas durante la sesión.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SesionPsicologoResponse", description = "Sesión terapéutica — vista psicólogo")
public class SesionPsicologoResponseDTO {

    /** Nombre de pila del paciente atendido en la sesión. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente atendido en la sesión. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;
    /** Fecha y hora en que se realizó la sesión. */
    @Schema(description = "Fecha de la sesión", example = "2026-04-01T11:00:00")

    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    @Schema(description = "Duración en minutos", example = "50")

    private Integer durationMinutes;

    /** Notas clínicas registradas durante la sesión. */
    @Schema(description = "Notas clínicas", example = "Mejora significativa")

    private String notas;

    /** Recomendaciones emitidas al finalizar la sesión. */
    @Schema(description = "Recomendaciones", example = "Ejercicios de relajación")

    private String recomendaciones;
}

