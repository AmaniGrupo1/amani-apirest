package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonIgnoreProperties({"usuario", "psicologo", "direcciones", "citas", "historiales", "respuestas"})
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    // Relación con psicólogo
    @JsonIgnoreProperties({"usuario", "pacientes", "citas"})
    @ManyToOne
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Psicologo psicologo;

    // Fecha de inicio de la asignación
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio = LocalDateTime.now();

    // Fecha de fin de la asignación (null si aún está activo)
    private LocalDateTime fechaFin;
}
