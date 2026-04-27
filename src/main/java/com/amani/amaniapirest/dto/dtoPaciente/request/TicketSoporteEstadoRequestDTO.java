package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO de solicitud para actualizar el estado de un ticket de soporte.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSoporteEstadoRequestDTO {

    /** Nuevo estado del ticket. */
    @NotNull(message = "El estado es obligatorio")
    private EstadoTicketSoporte estado;
}
