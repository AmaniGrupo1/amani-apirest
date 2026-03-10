package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;
    private LocalDateTime startDatetime;
    private int durationMinutes;
    @Enumerated(EnumType.STRING)
    private EstadoCita estado;
    private String motivo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_psicologo")
    private Psicologo psicologo;
    @OneToOne(mappedBy = "cita")
    private Sesion sesion;
}
