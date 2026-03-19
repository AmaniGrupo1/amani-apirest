package com.amani.amaniapirest.dto.dtoPregunta.psicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RespuestaPacientePsicologoResponseDTO {
    private String nombrePaciente;

    private String pregunta;

    private String respuesta;

    private String opcion;

    private LocalDateTime creadoEn;

}
