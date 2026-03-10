package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "progreso_emocional")
public class ProgresoEmocional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProgress;
    private LocalDate fecha;
    private int nivelEstres;
    private int nivelAnsiedad;
    private int nivelAnimo;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
