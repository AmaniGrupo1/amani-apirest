package com.amani.amaniapirest.dto.terapiasDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * DTO para TerapiaResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos TerapiaResponseDTO")
public class TerapiaResponseDTO {
    private Long idTipo;
    private String nombre;
    private Integer duracionMinutos;
    private BigDecimal precio;
}
