package com.amani.amaniapirest.dto.historialCita;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
