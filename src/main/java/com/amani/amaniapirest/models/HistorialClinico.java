package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que almacena registros del historial clinico de un paciente.
 *
 * <p>Cada entrada del historial incluye un título, diagnóstico, tratamiento
 * aplicado, observaciones del profesional y la fecha de creación.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "historial_clinico")
public class HistorialClinico {

    /** Identificador unico del registro del historial. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistory;

    /** Título o encabezado del registro clinico. */
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    private String diagnostico;

    /** Descripción del tratamiento prescrito o aplicado. */
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    private String observaciones;

    /** Fecha y hora de creación del registro clinico. */
    private LocalDateTime creadoEn;

    /** Paciente al que pertenece este registro clinico. */
    @JsonIgnoreProperties({"usuario", "psicologo", "direcciones", "citas", "historiales", "respuestas"})
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
