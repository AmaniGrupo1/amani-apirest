package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un tutor legal de un paciente.
 *
 * <p>Almacena los datos de contacto y la relacion familiar o legal
 * con el paciente, incluyendo nombre, telefono, email y dni.</p>
 */
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
