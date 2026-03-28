package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class DisponibilidadPsicologoDTO {
    private List<FranjaDisponible> franjas;

    public DisponibilidadPsicologoDTO() {}
    public DisponibilidadPsicologoDTO(List<FranjaDisponible> franjas) { this.franjas = franjas; }

    @Setter
    @Getter
    public static class FranjaDisponible {
        private LocalTime horaInicio;
        private LocalTime horaFin;
        public FranjaDisponible() {}
        public FranjaDisponible(LocalTime horaInicio, LocalTime horaFin) {
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }

    }
}

