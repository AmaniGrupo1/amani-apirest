package com.amani.amaniapirest.dto.dtoPsicologo.request;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para que el psicólogo actualice el estado de una {@code Cita}.
 *
 * <p>El psicólogo puede confirmar, cancelar o marcar como completada una cita
 * que le ha sido asignada. No puede modificar la fecha ni el paciente.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaPsicologoRequestDTO {

    /**
     * Nuevo estado de la cita.
     * <p>El psicólogo solo puede transitar entre: {@code confirmada},
     * {@code cancelada} y {@code completada}.</p>
     */
    private EstadoCita estado;
}

