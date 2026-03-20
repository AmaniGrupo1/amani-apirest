package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida con los datos de una entrada del diario emocional.
 *
 * <p>Incluye el identificador del paciente, la emoción registrada,
 * su intensidad, la nota y la fecha del registro.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DiarioEmocionResponse", description = "Entrada del diario emocional — vista paciente")
public class DiarioEmocionResponseDTO {
    /** Fecha y hora en que se registró la emoción. */
    @Schema(description = "Fecha y hora del registro", example = "2026-03-20T09:00:00")

    private LocalDateTime fecha;
    /** Nombre de la emoción registrada. */
    @Schema(description = "Emoción registrada", example = "alegría")

    private String emocion;
    /** Intensidad de la emoción en escala del 1 al 10. */
    @Schema(description = "Intensidad de la emoción (1-10)", example = "7")

    private Integer intensidad;
    /** Nota o comentario adicional del paciente. */
    @Schema(description = "Nota adicional del paciente", example = "Día productivo")

    private String nota;
}
