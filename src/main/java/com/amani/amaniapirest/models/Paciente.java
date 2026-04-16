package com.amani.amaniapirest.models;

import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Entidad que representa el perfil clínico de un paciente.
 *
 * <p>Contiene información demográfica y relaciones con sus citas,
 * historial clínico y direcciones. Cada paciente está vinculado a un
 * {@link Usuario} del sistema para autenticación e identidad.</p>
 */
@JsonIgnoreProperties({"usuario", "psicologo"})
@Entity
@Getter
@Setter
@ToString(exclude = {"usuario", "psicologo", "direcciones", "citas", "historiales", "respuestas"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pacientes")
public class Paciente {

    /** Identificador unico del paciente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;

    /** Fecha de nacimiento del paciente. */
    private LocalDate fechaNacimiento;

    /** Género del paciente (p.ej. "masculino", "femenino", "no binario"). */
    private String genero;

    /** Número de telefono de contacto del paciente. */
    private String telefono;

    /** Fecha y hora de creación del perfil. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora de la ultima actualización del perfil. */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Usuario del sistema asociado a este paciente. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_psicologo")
    private Psicologo psicologo;

    /** Lista de direcciones registradas para el paciente. */
    @JsonIgnoreProperties({"paciente"})
    @OneToMany(mappedBy = "paciente")
    private List<Direccion> direcciones;

    /** Lista de citas del paciente. */
    @JsonIgnoreProperties({"paciente", "psicologo"})
    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas;

    /** Historial clinico del paciente. */
    @JsonIgnoreProperties({"paciente"})
    @OneToMany(mappedBy = "paciente")
    private List<HistorialClinico> historiales;

    @JsonIgnoreProperties({"paciente"})
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Respuesta> respuestas;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private Set<PacienteSituacion> pacienteSituaciones;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Consentimiento> consentimientos;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private Set<Tutor> tutores;

}
