package com.amani.amaniapirest.dto.dtoPsicologo;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaPsicologoDTO {
    private Long idCita;
    private Long idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private LocalDateTime startDatetime;
    private Integer durationMinutes;
    private EstadoCita estadoCita;
    private String motivo;
}