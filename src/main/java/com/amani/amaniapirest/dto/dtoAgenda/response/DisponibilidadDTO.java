package com.amani.amaniapirest.dto.dtoAgenda.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
