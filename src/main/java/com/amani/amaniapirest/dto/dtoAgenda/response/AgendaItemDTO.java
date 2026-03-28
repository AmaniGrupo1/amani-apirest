package com.amani.amaniapirest.dto.dtoAgenda.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
