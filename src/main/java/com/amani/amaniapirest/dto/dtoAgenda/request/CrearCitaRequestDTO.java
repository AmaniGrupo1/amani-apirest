package com.amani.amaniapirest.dto.dtoAgenda.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearCitaRequestDTO {
    private Long idPsicologo;
    private Long idPaciente;
    private String startDatetime;
    private Integer duracionMinutos;
    private String motivo;
}
