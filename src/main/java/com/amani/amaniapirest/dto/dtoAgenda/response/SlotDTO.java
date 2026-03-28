package com.amani.amaniapirest.dto.dtoAgenda.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
