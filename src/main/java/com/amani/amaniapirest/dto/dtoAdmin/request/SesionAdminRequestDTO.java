package com.amani.amaniapirest.dto.dtoAdmin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para que el administrador cree o actualice una {@code Sesion}.
 *
 * <p>Vincula la sesión a una cita existente y permite registrar o corregir
 * los datos clínicos como notas y recomendaciones.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionAdminRequestDTO {

    /**
     * Identificador de la {@code Cita} de la que deriva esta sesión.
     * <p>La cita debe existir y estar en estado {@code completada}.</p>
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

