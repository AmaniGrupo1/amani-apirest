package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.enums.CategoriaTicketSoporte;
import com.amani.amaniapirest.enums.TipoTicketSoporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de solicitud para crear un nuevo ticket de soporte.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSoporteRequestDTO {

    /** Titulo breve del ticket. */
    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 200, message = "El titulo no puede exceder 200 caracteres")
    private String titulo;

    /** Descripcion detallada del problema o consulta. */
    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 2000, message = "La descripcion no puede exceder 2000 caracteres")
    private String descripcion;

    /** Tipo del ticket. */
    @NotNull(message = "El tipo es obligatorio")
    private TipoTicketSoporte tipo;

    /** Categoria del ticket. */
    @NotNull(message = "La categoria es obligatoria")
    private CategoriaTicketSoporte categoria;
}
