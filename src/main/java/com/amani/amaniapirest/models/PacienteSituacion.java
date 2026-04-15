package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de union entre {@link Paciente} y {@link Situacion}.
 *
 * <p>Representa la relacion many-to-many entre pacientes y las situaciones
 * que estan experimentando, permitiendo el seguimiento de contextos
 * emocionales especficos.</p>
 *
 * @see Paciente
 * @see Situacion
 */
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
