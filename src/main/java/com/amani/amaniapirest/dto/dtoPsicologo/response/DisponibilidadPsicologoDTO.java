package com.amani.amaniapirest.dto.dtoPsicologo.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

/**
 * DTO de respuesta para representar la disponibilidad de un psicólogo.
 *
 * <p>Contiene una lista de franjas horarias en las que el psicólogo
 * se encuentra disponible para atender pacientes.</p>
 */
@Getter
@Setter
public class DisponibilidadPsicologoDTO {
    private List<FranjaDisponible> franjas;

    public DisponibilidadPsicologoDTO() {}
    /**
     * Creates a new instance of {@code DisponibilidadPsicologoDTO} with the specified franjas.
     *
     * @param franjas  lista de franjas horarias disponibles
     */
    public DisponibilidadPsicologoDTO(List<FranjaDisponible> franjas) { this.franjas = franjas; }

    /**
     * DTO interno que representa una franja horaria disponible.
     *
     * <p>Define el rango horario de disponibilidad del psicólogo.</p>
     */
    @Setter
    @Getter
    public static class FranjaDisponible {
        private LocalTime horaInicio;
        private LocalTime horaFin;
        public FranjaDisponible() {}
        /**
         * Creates a new instance of {@code FranjaDisponible} with the specified parameters.
         *
         * @param horaInicio  hora de inicio de la franja
         * @param horaFin     hora de finalización de la franja
         */
        public FranjaDisponible(LocalTime horaInicio, LocalTime horaFin) {
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }

    }
}

