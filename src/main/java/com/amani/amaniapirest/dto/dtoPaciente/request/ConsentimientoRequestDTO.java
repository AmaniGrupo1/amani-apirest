package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para ConsentimientoRequestDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos ConsentimientoRequestDTO")
public class ConsentimientoRequestDTO {
    private boolean aceptaTerminos;
    private boolean aceptaVideoconferencia;
    private boolean aceptaComunicacion;
}
