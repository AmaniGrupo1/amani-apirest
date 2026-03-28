package com.amani.amaniapirest.dto.dtoAgenda.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioRequestDTO {
    private List<FranjaHorarioDTO> franjas;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FranjaHorarioDTO {
        private Short diaSemana;
        private String horaInicio;
        private String horaFin;
        private boolean activo;
    }
}
