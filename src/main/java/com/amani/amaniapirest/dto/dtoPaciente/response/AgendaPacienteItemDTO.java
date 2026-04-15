package com.amani.amaniapirest.dto.dtoPaciente.response;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de respuesta para representar un item de agenda de un paciente.
 *
 * <p>Contiene la información detallada de una cita o evento en la agenda
 * del paciente, incluyendo fecha, horario, estado y motivo.</p>
 */
public class AgendaPacienteItemDTO {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String motivo;
    private Long idCita;

    public AgendaPacienteItemDTO() {}

    /**
     * Creates a new instance of {@code AgendaPacienteItemDTO} with the specified parameters.
     *
     * @param fecha       fecha de la cita
     * @param horaInicio  hora de inicio de la cita
     * @param horaFin     hora de finalización de la cita
     * @param estado      estado actual de la cita (ej. "pendiente", "confirmada", "cancelada")
     * @param motivo      motivo o descripción de la cita
     * @param idCita      identificador único de la cita en el sistema
     */
    public AgendaPacienteItemDTO(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String estado, String motivo, Long idCita) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.motivo = motivo;
        this.idCita = idCita;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Long getIdCita() { return idCita; }
    public void setIdCita(Long idCita) { this.idCita = idCita; }
}

