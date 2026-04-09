package com.amani.amaniapirest.dto.dtoAgenda.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItemDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipo;
    private String estado;
    private String motivo;
    private Integer duracionMinutos;
    private String nombrePaciente;
    private String nombrePsicologo;
}
