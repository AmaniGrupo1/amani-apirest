package com.amani.amaniapirest.dto.situacion;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para crear o actualizar una situación psicosocial.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SituacionRequest {

    private String nombre;

    private String categoria;

    private String descripcion;

    private Boolean activo = true;
}
