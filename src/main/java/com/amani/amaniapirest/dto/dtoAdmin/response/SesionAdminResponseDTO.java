package com.amani.amaniapirest.dto.dtoAdmin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre una {@code Sesion}.
 *
 * <p>Muestra el detalle completo de la sesión terapéutica, incluyendo los
 * identificadores y nombres del paciente y del psicólogo, las notas clínicas
 * y las recomendaciones emitidas.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SesionAdminResponse", description = "Sesión terapéutica — vista administrador")
public class SesionAdminResponseDTO {

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

    /** Identificador del psicólogo que realizó la sesión. */
    @Schema(description = "Nombre del psicólogo", example = "Ana")

    private String nombrePsicologo;

    /** Nombre de pila del psicólogo. */
    @Schema(description = "Apellido del psicólogo", example = "García")

    private String apellidoPsicologo;


    /** Fecha y hora en que se realizó la sesión. */
    @Schema(description = "Fecha de la sesión", example = "2026-04-01T11:00:00")

    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    @Schema(description = "Duración en minutos", example = "50")

    private Integer durationMinutes;

    /** Notas clínicas registradas durante la sesión. */
    @Schema(description = "Notas clínicas", example = "Mejora visible")

    private String notas;

    /** Recomendaciones emitidas al finalizar la sesión. */
    @Schema(description = "Recomendaciones", example = "Continuar ejercicios")

    private String recomendaciones;

    /** Fecha y hora de creación del registro. */
    @Schema(description = "Fecha de creación", example = "2026-04-01T12:00:00")

    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del registro. */
    @Schema(description = "Última actualización", example = "2026-04-01T12:00:00")

    private LocalDateTime updatedAt;
}

