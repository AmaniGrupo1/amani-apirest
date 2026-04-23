package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para registrar entradas del diario emocional de un paciente.
 *
 * <p>Permite al paciente llevar un seguimiento de sus estados emocionales
 * indicando la emocion experimentada, su intensidad y notas adicionales.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diario_emociones")
public class DiarioEmocion {

    /** Identificador unico de la entrada del diario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDiario;

    /** Titulo de la entrada del diario. */
    @Column(nullable = false)
    private String titulo;

    /** Fecha y hora en que se registro la emocion. */
    private LocalDateTime fecha;

    /** Nombre o descripción de la emocion registrada (p.ej. "alegría", "tristeza"). */
    private String emocion;

    /** Nivel de intensidad de la emocion en una escala numerica. */
    private int intensidad;

    /** Nota o comentario libre del paciente sobre la emocion. */
    private String nota;

    /** Paciente al que pertenece esta entrada del diario. */
    @JsonIgnoreProperties({"usuario", "psicologo", "direcciones", "citas", "historiales", "respuestas"})
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
