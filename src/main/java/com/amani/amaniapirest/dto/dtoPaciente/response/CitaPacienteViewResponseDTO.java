package com.amani.amaniapirest.dto.dtoPaciente.response;

import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.enums.ModalidadCita;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CitaPacienteViewResponse", description = "Vista optimizada de cita para paciente")
public class CitaPacienteViewResponseDTO {

    private Long idCita;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private Integer durationMinutes;

    private EstadoCita estado;

    private ModalidadCita modalidad;

    private String motivo;

    private String tipoTerapia;

    // 🔔 útil para notificaciones/UI
    private Long minutosRestantes;

    private Boolean esProxima;
}