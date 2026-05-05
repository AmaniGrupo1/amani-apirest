package com.amani.amaniapirest.dto.terapiasDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerapiaRequestDTO {
    private String nombre;
    private Integer duracionMinutos;
    private BigDecimal precio;
}
