package com.amani.amaniapirest.dto.dtoPsicologo.response;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaPsicologoItemDTO {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipo; // "cita" o "bloqueo"
    private String detalle;
    private Long referenciaId;

    public AgendaPsicologoItemDTO() {}

    public AgendaPsicologoItemDTO(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String tipo, String detalle, Long referenciaId) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tipo = tipo;
        this.detalle = detalle;
        this.referenciaId = referenciaId;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
}

