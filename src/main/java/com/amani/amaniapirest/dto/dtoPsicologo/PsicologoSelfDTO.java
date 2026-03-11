package com.amani.amaniapirest.dto.dtoPsicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoSelfDTO {
    private Long idPsicologo;
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String licencia;
}