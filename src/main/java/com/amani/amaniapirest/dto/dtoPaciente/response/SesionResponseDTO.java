package com.amani.amaniapirest.dto.dtoPaciente.response;

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
public class SesionResponseDTO {


    /** Fecha y hora en que se realizó la sesión. */
    private LocalDateTime sessionDate;

    /** Duración efectiva de la sesión en minutos. */
    private Integer durationMinutes;

    /** Notas clínicas registradas por el psicólogo durante la sesión. */
    private String notas;

    /** Recomendaciones emitidas por el psicólogo al finalizar la sesión. */
    private String recomendaciones;
}
