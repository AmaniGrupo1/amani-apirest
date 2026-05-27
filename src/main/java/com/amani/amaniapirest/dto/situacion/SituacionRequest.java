package com.amani.amaniapirest.dto.situacion;

import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para crear o actualizar una situación psicosocial.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos SituacionRequest")
public class SituacionRequest {

    private String nombre;

    private String categoria;

    private String descripcion;

    private Boolean activo = true;
}
