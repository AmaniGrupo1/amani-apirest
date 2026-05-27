package com.amani.amaniapirest.dto.dtoPsicologo.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de respuesta para representar un item de agenda de un psicólogo.
 *
 * <p>Contiene la información de eventos en la agenda del psicólogo,
 * incluyendo citas y bloqueos, con detalles sobre tipo y referencia.</p>
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos AgendaPsicologoItemDTO")
public class AgendaPsicologoItemDTO {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipo; // "cita" o "bloqueo"
    private String detalle;
    private Long referenciaId;
}

