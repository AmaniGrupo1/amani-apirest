package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para registrar una entrada en el diario emocional.
 *
 * <p>Los campos {@code idPaciente}, {@code emocion} e {@code intensidad}
 * son obligatorios. La fecha y la nota son opcionales.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiarioEmocionRequestDTO {

    /** Identificador del paciente que registra la entrada. */
    @NotNull
    private Long idPaciente;

    /** Fecha y hora del registro. Si es nula, el servicio usa la fecha actual. */
    private LocalDateTime fecha;

    /** Nombre de la emoción experimentada (p.ej. "alegría", "tristeza"). */
    @NotBlank
    private String emocion;

    /** Intensidad de la emoción en una escala del 1 al 10. */
    @NotNull
    @Min(1) @Max(10)
    private Integer intensidad;

    /** Nota o comentario libre del paciente sobre la emoción. */
    private String nota;
}
