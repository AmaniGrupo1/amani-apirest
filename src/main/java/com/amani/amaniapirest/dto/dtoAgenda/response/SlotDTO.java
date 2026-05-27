package com.amani.amaniapirest.dto.dtoAgenda.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

import java.time.LocalTime;

/**
 * DTO de respuesta para representar un slot horario en la agenda.
 *
 * <p>Define un periodo de tiempo específico con su estado (libre u ocupado)
 * y descripción, utilizado para la gestión detallada de la disponibilidad.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos SlotDTO")
public class SlotDTO {
    private LocalTime hora;
    private LocalTime horaFin; // agregado
    private boolean ocupado;
    private String descripcion;
}
