package com.amani.amaniapirest.dto.dtoAdmin.response;

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
public class SesionAdminResponseDTO {

    /** Nombre de pila del paciente. */
    private String nombrePaciente;

    /** Apellido del paciente. */
    private String apellidoPaciente;

    /** Identificador del psicólogo que realizó la sesión. */
    private String nombrePsicologo;

    /** Nombre de pila del psicólogo. */
    private String apellidoPsicologo;


    /** Fecha y hora en que se realizó la sesión. */
    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    private Integer durationMinutes;

    /** Notas clínicas registradas durante la sesión. */
    private String notas;

    /** Recomendaciones emitidas al finalizar la sesión. */
    private String recomendaciones;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del registro. */
    private LocalDateTime updatedAt;
}

