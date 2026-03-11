package com.amani.amaniapirest.dto.dtoAdmin.response;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre una {@code Cita}.
 *
 * <p>Muestra la información completa de la cita, incluyendo los datos del
 * paciente y del psicólogo involucrados, el estado actual y los metadatos
 * de auditoría.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaAdminResponseDTO {

    /** Identificador único de la cita. */
    private Long idCita;

    /** Identificador del paciente que asiste a la cita. */
    private Long idPaciente;

    /** Nombre de pila del paciente. */
    private String nombrePaciente;

    /** Apellido del paciente. */
    private String apellidoPaciente;

    /** Identificador del psicólogo que atiende la cita. */
    private Long idPsicologo;

    /** Nombre de pila del psicólogo. */
    private String nombrePsicologo;

    /** Apellido del psicólogo. */
    private String apellidoPsicologo;

    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    private Integer durationMinutes;

    /** Estado actual de la cita. */
    private EstadoCita estadoCita;

    /** Motivo o descripción de la consulta. */
    private String motivo;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última modificación del registro. */
    private LocalDateTime updatedAt;
}

