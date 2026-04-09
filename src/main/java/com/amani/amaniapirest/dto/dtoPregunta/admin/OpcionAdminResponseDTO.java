package com.amani.amaniapirest.dto.dtoPregunta.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para la vista de administrador sobre una pregunta del cuestionario.
 *
 * <p>Muestra el texto de la pregunta, su tipo (abierta, opción múltiple, etc.),
 * las opciones disponibles si las tiene y la fecha de creación.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "OpcionAdminResponse", description = "Pregunta del cuestionario — vista administrador")
public class OpcionAdminResponseDTO {

    /** Enunciado de la pregunta. */
    @Schema(description = "Enunciado de la pregunta", example = "¿Cómo te sientes hoy?")

    private String texto;

    /** Tipo de pregunta (p.ej. "abierta", "opciones", "escala"). */
    @Schema(description = "Tipo de pregunta", example = "opciones")

    private String tipo;

    /** Lista de opciones de respuesta disponibles para la pregunta. */
    @Schema(description = "Opciones disponibles")

    private List<String> opciones;

    /** Fecha y hora de creación de la pregunta. */
    @Schema(description = "Fecha de creación", example = "2026-01-10T08:00:00")

    private LocalDateTime creadoEn;
}
