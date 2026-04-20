package com.amani.amaniapirest.dto.terapiasDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TerapiaResponseDTO {
    private Long idTipo;
    private String nombre;
    private Integer duracionMinutos;
    private BigDecimal precio;
}
