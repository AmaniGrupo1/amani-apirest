package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad para registrar el progreso emocional periodico de un paciente.
 *
 * <p>Almacena niveles numéricos de estrés, ansiedad y ánimo en una fecha
 * determinada, permitiendo al psicólogo hacer seguimiento de la evolución
 * emocional del paciente a lo largo del tiempo.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "progreso_emocional")
public class ProgresoEmocional {

    /** Identificador unico del registro de progreso. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProgress;

    /** Fecha en que se registró el progreso emocional. */
    private LocalDate fecha;

    /** Nivel de estrés del paciente (escala numerica). */
    private int nivelEstres;

    /** Nivel de ansiedad del paciente (escala numerica). */
    private int nivelAnsiedad;

    /** Nivel de ánimo general del paciente (escala numerica). */
    private int nivelAnimo;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime createdAt;

    /** Paciente al que pertenece este registro de progreso. */
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
