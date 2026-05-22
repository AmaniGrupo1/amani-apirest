package com.amani.amaniapirest.dto.historialClinico;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para HistorialClinicoResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos HistorialClinicoResponseDTO")
public class HistorialClinicoResponseDTO {

    private Long id;
    private String titulo;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime creadoEn;
}
