package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table(name = "bloqueos_agenda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueoAgenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloqueo")
    private Long idBloqueo;
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
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
