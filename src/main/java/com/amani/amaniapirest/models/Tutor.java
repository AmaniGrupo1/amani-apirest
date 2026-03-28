package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tutores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tutor")
    private Long idTutor;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    private String email;

    private String dni;

    @Column(nullable = false)
    private String tipo; // MADRE / PADRE / TUTOR
}
