package com.amani.amaniapirest.dto.dtoPsicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionPsicologoDTO {
    private Long idSesion;
    private Long idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private LocalDateTime sessionDate;
    private Integer durationMinutes;
    private String notas;
    private String recomendaciones;
}
