package com.amani.amaniapirest.dto.dtoAgenda.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueoRequestDTO {
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String motivo;
}
