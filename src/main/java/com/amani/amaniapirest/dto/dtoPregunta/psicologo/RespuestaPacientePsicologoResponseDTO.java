package com.amani.amaniapirest.dto.dtoPregunta.psicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RespuestaPacientePsicologoResponseDTO {
    private String nombre;
    private String pregunta;
    private String respuesta;
    private String opcion;
    private String creadoEn;
}
