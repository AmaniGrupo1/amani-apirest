package com.amani.amaniapirest.dto.dtoAgenda.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
