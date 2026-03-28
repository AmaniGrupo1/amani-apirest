package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "DiarioEmocionRequest", description = "Datos para registrar una entrada en el diario emocional")
public class DiarioEmocionRequestDTO {

    /** Identificador del paciente que registra la entrada. */
    @NotNull
    @Schema(description = "Identificador del paciente que registra la entrada", example = "1")
    private Long idPaciente;

    /** Fecha y hora del registro. Si es nula, el servicio usa la fecha actual. */
    @Schema(description = "Fecha y hora del registro; si es nula se usa la fecha actual", example = "2026-03-20T09:00:00")
    private LocalDateTime fecha;

    /** Nombre de la emoción experimentada (p.ej. "alegría", "tristeza"). */
    @NotBlank
    @Schema(description = "Nombre de la emoción experimentada", example = "alegría")
    private String emocion;

    /** Intensidad de la emoción en una escala del 1 al 10. */
    @NotNull
    @Min(1) @Max(10)
    @Schema(description = "Intensidad de la emoción (escala 1-10)", example = "7")
    private Integer intensidad;

    /** Nota o comentario libre del paciente sobre la emoción. */
    @Schema(description = "Nota o comentario libre del paciente", example = "Hoy me sentí muy bien después de hacer ejercicio")
    private String nota;
}
