package com.amani.amaniapirest.dto.historialClinico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoResponseDTO {

    private Long id;
    private String titulo;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime creadoEn;
}
