package com.amani.amaniapirest.models;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsicologoPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con paciente
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    // Relación con psicólogo
    @ManyToOne
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Psicologo psicologo;

    // Fecha de inicio de la asignación
    private LocalDateTime fechaInicio = LocalDateTime.now();

    // Fecha de fin de la asignación (null si aún está activo)
    private LocalDateTime fechaFin;
}
