package com.amani.amaniapirest.dto.terapiasDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TerapiaResponseDTO {
    private Long idTipo;
    private String nombre;
    private Integer duracionMinutos;
}
