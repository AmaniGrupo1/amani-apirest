package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con los datos de una entrada del diario emocional.
 *
 * <p>Incluye el identificador del paciente, la emoción registrada,
 * su intensidad, la nota y la fecha del registro.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiarioEmocionResponseDTO {

    /** Identificador único de la entrada del diario. */
    private Long idDiario;

    /** Identificador del paciente al que pertenece la entrada. */
    private Long idPaciente;

    /** Fecha y hora en que se registró la emoción. */
    private LocalDateTime fecha;

    /** Nombre de la emoción registrada. */
    private String emocion;

    /** Intensidad de la emoción en escala del 1 al 10. */
    private Integer intensidad;

    /** Nota o comentario adicional del paciente. */
    private String nota;
}
