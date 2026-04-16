package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.EstadoCita;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", columnDefinition = "estado_cita", nullable = false)
    private EstadoCita estado;

    /** Motivo o descripción de la consulta. */
    private String motivo;

    /** Fecha y hora de creación del registro. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @JsonIgnoreProperties({"usuario", "psicologo", "paciente"})
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    /** Psicologo que atiende la cita. */
    @JsonIgnoreProperties({"usuario", "pacientes"})
    @ManyToOne
    @JoinColumn(name = "id_psicologo")
    private Psicologo psicologo;

    /** Sesion terapéutica generada a partir de esta cita. */
    @JsonIgnoreProperties({"cita"})
    @OneToOne(mappedBy = "cita")
    private Sesion sesion;

    @ManyToOne
    @JoinColumn(name = "id_tipo_terapia", nullable = false)
    private TiposTerapia tipoTerapia;

    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"cita"})
    private Pago pago;

}
