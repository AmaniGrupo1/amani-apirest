package com.amani.amaniapirest.dto.dtoPaciente.response;

import com.amani.amaniapirest.enums.CategoriaTicketSoporte;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.enums.TipoTicketSoporte;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con los datos de un ticket de soporte.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSoporteResponseDTO {

    /** Identificador unico del ticket. */
    private Long idTicket;

    /** Titulo breve del ticket. */
    private String titulo;

    /** Descripcion detallada del problema o consulta. */
    private String descripcion;

    /** Tipo del ticket. */
    private TipoTicketSoporte tipo;

    /** Categoria del ticket. */
    private CategoriaTicketSoporte categoria;

    /** Estado actual del ticket. */
    private EstadoTicketSoporte estado;

    /** Fecha y hora de creacion. */
    private LocalDateTime creadoEn;

    /** Fecha y hora de la ultima actualizacion. */
    private LocalDateTime actualizadoEn;

    /** Fecha y hora de cierre, o {@code null} si sigue abierto. */
    private LocalDateTime cerradoEn;

    /** Identificador del usuario que creo el ticket. */
    private Long idUsuario;

    /** Nombre completo del usuario que creo el ticket. */
    private String nombreUsuario;
}
