package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa el perfil profesional de un psicologo.
 *
 * <p>Contiene información sobre la especialidad, experiencia, descripcion
 * y licencia del profesional. Cada psicologo está vinculado a un
 * {@link Usuario} del sistema y puede tener multiples {@link Cita citas} asignadas.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psicologos")
public class Psicologo {

    /** Identificador unico del psicologo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPsicologo;

    /** Especialidad o área de enfoque del psicologo (p.ej. "ansiedad", "trauma"). */
    private String especialidad;

    /** Años de experiencia profesional. */
    private int experiencia;

    /** Descripción breve del psicologo y su enfoque terapéutico. */
    private String descripcion;

    /** Numero de licencia o colegiación profesional. */
    private String licencia;

    /** Fecha y hora de creación del perfil. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la ultima actualización del perfil. */
    private LocalDateTime updatedAt;

    /** Usuario del sistema asociado a este psicologo. */
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /** Lista de citas asignadas a este psicologo. */
    @OneToMany(mappedBy = "psicologo")
    private List<Cita> citas;
}
