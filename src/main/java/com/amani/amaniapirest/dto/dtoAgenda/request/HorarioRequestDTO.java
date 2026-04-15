package com.amani.amaniapirest.dto.dtoAgenda.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la solicitud de gestión de horarios de disponibilidad.
 *
 * <p>Utilizado para definir los días y franjas horarias en las que un psicólogo
 * está disponible para recibir pacientes. Permite activar o desactivar cada
 * franja horaria y especificar motivos de inactividad.</p>
 *
 * @param franjas lista de franjas horarias que definen la disponibilidad
 * @see FranjaHorarioDTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioRequestDTO {
    private List<FranjaHorarioDTO> franjas;

    /**
     * DTO para representar una franja horaria de disponibilidad.
     *
     * <p>Define un día de la semana y un intervalo de horas durante el cual
     * el psicólogo está disponible o no para atender pacientes.</p>
     *
     * @param diaSemana  día de la semana (1=Lunes, 7=Domingo)
     * @param horaInicio hora de inicio de la franja en formato HH:mm
     * @param horaFin    hora de finalización de la franja en formato HH:mm
     * @param activo     indica si la franja está activa (disponible)
     * @param motivo     descripción del motivo cuando la franja no está activa
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FranjaHorarioDTO {
        private Short diaSemana;
        private String horaInicio;
        private String horaFin;
        private boolean activo;
        private String motivo;
    }
}
