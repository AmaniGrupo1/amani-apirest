package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"usuario", "pacientes"})
@Entity
@Getter
@Setter
@ToString(exclude = {"usuario", "citas"})
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

    private Integer duracionDefault = 50; // Duración por defecto de las citas en minutos
    /** Fecha y hora de creación del perfil. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la ultima actualización del perfil. */
    private LocalDateTime updatedAt;

    /** Usuario del sistema asociado a este psicologo. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /** Lista de citas asignadas a este psicologo. */
    @JsonIgnoreProperties({"psicologo", "paciente"})
    @OneToMany(mappedBy = "psicologo")
    private List<Cita> citas;
}
