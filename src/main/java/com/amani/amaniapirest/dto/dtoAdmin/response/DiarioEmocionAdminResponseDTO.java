package com.amani.amaniapirest.dto.dtoAdmin.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre una entrada del diario emocional.
 *
 * <p>Incluye los datos de identificación del paciente, la emoción registrada,
 * su intensidad, la nota libre y las marcas de tiempo de auditoría.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DiarioEmocionAdminResponse", description = "Entrada del diario emocional — vista administrador")
public class DiarioEmocionAdminResponseDTO {

    /** Nombre del paciente */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

    /** Fecha y hora del registro */
    @Schema(description = "Fecha del registro", example = "2026-03-20T09:00:00")

    private LocalDateTime fecha;

    /** Emoción registrada */
    @Schema(description = "Emoción registrada", example = "alegría")

    private String emocion;

    /** Intensidad de la emoción (1-10) */
    @Schema(description = "Intensidad (1-10)", example = "7")

    private Integer intensidad;

    /** Nota o comentario del paciente */
    @Schema(description = "Nota del paciente", example = "Buen día")

    private String nota;

    /** Fecha y hora de creación del registro (auditoría) */
    @Schema(description = "Fecha de creación", example = "2026-03-20T09:00:00")

    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización (auditoría) */
    @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

    private LocalDateTime updatedAt;
}