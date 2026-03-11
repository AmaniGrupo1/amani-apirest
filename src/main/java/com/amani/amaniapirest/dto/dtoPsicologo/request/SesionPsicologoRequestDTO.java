package com.amani.amaniapirest.dto.dtoPsicologo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para que el psicólogo registre una nueva {@code Sesion}
 * a partir de una cita completada.
 *
 * <p>Vincula la sesión a la cita de origen y permite registrar la fecha,
 * la duración real, las notas clínicas y las recomendaciones emitidas.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionPsicologoRequestDTO {

    /**
     * Identificador de la {@code Cita} de la que deriva esta sesión.
     * <p>La cita debe estar en estado {@code completada}.</p>
     */
    private Long idCita;

    /** Fecha y hora en que se realizó la sesión. */
    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    private Integer durationMinutes;

    /** Notas clínicas registradas durante la sesión. */
    private String notas;

    /** Recomendaciones emitidas al finalizar la sesión. */
    private String recomendaciones;
}

