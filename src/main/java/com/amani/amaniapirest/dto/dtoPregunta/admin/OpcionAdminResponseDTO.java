package com.amani.amaniapirest.dto.dtoPregunta.admin;

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
public class OpcionAdminResponseDTO {

    /** Enunciado de la pregunta. */
    private String texto;

    /** Tipo de pregunta (p.ej. "abierta", "opciones", "escala"). */
    private String tipo;

    /** Lista de opciones de respuesta disponibles para la pregunta. */
    private List<String> opciones;

    /** Fecha y hora de creación de la pregunta. */
    private LocalDateTime creadoEn;
}
