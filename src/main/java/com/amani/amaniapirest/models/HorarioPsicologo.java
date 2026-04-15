package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que define el horario de atencion de un psicologo.
 *
 * <p>Establece los dias de la semana y las franjas horarias en las que
 * el psicologo se encuentra disponible para atender pacientes.</p>
 */
@Entity
@Table(name = "horario_psicologo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioPsicologo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Long idHorario;
    @JsonIgnoreProperties({"usuario", "pacientes", "citas"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Psicologo psicologo;
    /**
     * 0=lunes … 6=domingo
     */
    private Short diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    @Builder.Default
    private boolean activo = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
