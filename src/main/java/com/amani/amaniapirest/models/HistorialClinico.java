package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "historial_clinico")
public class HistorialClinico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistory;
    private String titulo;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime creadoEn;
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
