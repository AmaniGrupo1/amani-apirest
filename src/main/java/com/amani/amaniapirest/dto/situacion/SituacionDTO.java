package com.amani.amaniapirest.dto.situacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SituacionDTO {
    private Long idSituacion;
    private String nombre;
    private String categoria;
    private String descripcion;
}
