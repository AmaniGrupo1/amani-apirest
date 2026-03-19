package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entidad que modela una cita programada entre un paciente y un psicologo.
 *
 * <p>Registra la fecha y hora de inicio, la duración, el estado actual según
 * {@link EstadoCita}, el motivo de la consulta y la sesión resultante si la
 * cita fue completada.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "citas")
public class Cita {

    /** Identificador unico de la cita. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime startDatetime;

    /** Duración de la cita en minutos. */
    private int durationMinutes;


    private String estado;

    /** Motivo o descripción de la consulta. */
    private String motivo;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    /** Psicologo que atiende la cita. */
    @ManyToOne
    @JoinColumn(name = "id_psicologo")
    private Psicologo psicologo;

    /** Sesion terapéutica generada a partir de esta cita. */
    @OneToOne(mappedBy = "cita")
    private Sesion sesion;

}
