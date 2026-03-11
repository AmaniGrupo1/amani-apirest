package com.amani.amaniapirest.dto.dtoPsicologo.response;

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
public class SesionPsicologoResponseDTO {

    /** Identificador único de la sesión. */
    private Long idSesion;

    /** Identificador del paciente atendido. */
    private Long idPaciente;

    /** Nombre de pila del paciente. */
    private String nombrePaciente;

    /** Apellido del paciente. */
    private String apellidoPaciente;

    /** Fecha y hora en que se realizó la sesión. */
    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    private Integer durationMinutes;

    /** Notas clínicas registradas durante la sesión. */
    private String notas;

    /** Recomendaciones emitidas al finalizar la sesión. */
    private String recomendaciones;
}

