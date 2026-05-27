package com.amani.amaniapirest.dto.historialCita;

import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para HistorialCitaResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos HistorialCitaResponseDTO")
public class HistorialCitaResponseDTO {

    private Long idCita;

    private LocalDateTime fechaHora;

    private Integer duracionMinutos;

    private String estado;

    private String motivo;

    private String modalidad;

    private String nombrePaciente;

    private String nombrePsicologo;

    private String tipoTerapia;

    private BigDecimal precio;

    private String estadoPago;

}
