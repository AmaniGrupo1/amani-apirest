package com.amani.amaniapirest.dto.dtoPsicologo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte las entradas del diario emocional de un paciente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DiarioEmocionalPsicologoResponse", description = "Diario emocional de un paciente — vista psicólogo")
public class DiarioEmocionalPsicologoDTO {

    /** Identificador de la entrada en el diario emocional */
    @Schema(description = "Identificador de la entrada", example = "1")

    private Long idDiario;

    /** Identificador del paciente */
    @Schema(description = "Identificador del paciente", example = "1")

    private Long idPaciente;

    /** Nombre del paciente */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

    /** Fecha y hora del registro de la emoción */
    @Schema(description = "Fecha del registro", example = "2026-03-20T09:00:00")

    private LocalDateTime fecha;

    /** Nombre de la emoción */
    @Schema(description = "Emoción registrada", example = "alegría")

    private String emocion;

    /** Intensidad de la emoción (1-10) */
    @Schema(description = "Intensidad (1-10)", example = "7")

    private Integer intensidad;

    /** Nota o comentario adicional */
    @Schema(description = "Nota adicional", example = "Buen día")

    private String nota;
}
