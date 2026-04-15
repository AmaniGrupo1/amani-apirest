package com.amani.amaniapirest.dto.dtoAgenda.response;

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
public class DisponibilidadDTO {
    private LocalDate fecha;
    private boolean diaCompleto;
    private List<SlotDTO> slotsLibres;
}
