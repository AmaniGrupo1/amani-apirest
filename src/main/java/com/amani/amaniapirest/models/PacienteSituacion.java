package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "paciente_situacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteSituacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // RELACION CON PACIENTE
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    // =========================
    // RELACION CON SITUACION
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situacion", nullable = false)
    private Situacion situacion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
