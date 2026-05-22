package com.amani.amaniapirest.dto.dtoAgenda.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta para representar la disponibilidad de un día en la agenda.
 *
 * <p>Contiene la fecha y las franjas horarias disponibles (slots) para
 * programar citas, indicando si el día está completamente disponible o parcial.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos DisponibilidadDTO")
public class DisponibilidadDTO {
    private LocalDate fecha;
    private boolean diaCompleto;
    private List<SlotDTO> slotsLibres;
}
