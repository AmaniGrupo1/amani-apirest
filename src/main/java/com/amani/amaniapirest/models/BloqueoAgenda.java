package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa un bloqueo de agenda del psicologo.
 *
 * <p>Permite al psicologo marcar periods de tiempo como no disponibles
 * para la reserva de citas, ya sea por completo o por fracciones horarias.</p>
 */
@Entity
@Table(name = "bloqueos_agenda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueoAgenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloqueo")
    private Long idBloqueo;
    @JsonIgnoreProperties({"usuario", "pacientes", "citas"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Psicologo psicologo;
    private LocalDate fecha;
    /**
     * NULL = día completo bloqueado
     */
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivo;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
