package com.amani.amaniapirest.dto.dtoAgenda.response;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private LocalTime hora;
    private LocalTime horaFin; // agregado
    private boolean ocupado;
    private String descripcion;
}
