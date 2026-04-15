package com.amani.amaniapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que registra el detalle de una sesion terapéutica.
 *
 * <p>Cada sesion está vinculada a una {@link Cita} completada y almacena
 * la fecha de realización, duración, notas clinicas y recomendaciones
 * elaboradas por el psicólogo durante la atención.</p>
 *
 * @see Cita
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sesiones")
public class Sesion {

    /** Identificador unico de la sesion. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSesion;


    @JsonIgnoreProperties({"usuario", "psicologo", "paciente"})
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @JsonIgnoreProperties({"usuario", "pacientes"})
    @ManyToOne
    @JoinColumn(name = "id_psicologo")
    private Psicologo psicologo;

    /** Fecha y hora en que se realizo la sesion. */
    private LocalDateTime sessionDate;

    /** Duracion efectiva de la sesion en minutos. */
    private int durationMinutes;

    /** Notas clinicas registradas por el psicologo durante la sesion. */
    private String notas;

    /** Recomendaciones emitidas por el psicologo al finalizar la sesion. */
    private String recomendaciones;

    /** Fecha y hora de creacion del registro. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la ultima actualizacion del registro. */
    private LocalDateTime updatedAt;

    /** Cita terapeutica de la que deriva esta sesion. */
    @JsonIgnoreProperties({"paciente", "psicologo", "sesion"})
    @OneToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;
}
