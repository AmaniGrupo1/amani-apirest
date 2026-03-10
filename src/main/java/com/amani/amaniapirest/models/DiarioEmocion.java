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
@Table(name = "diario_emociones")
public class DiarioEmocion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDiario;
    private LocalDateTime fecha;
    private String emocion;
    private int intensidad;
    private String nota;
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
