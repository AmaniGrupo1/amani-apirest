package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa una situacion o circunstancia que puede afectar
 * la salud emocional del paciente.
 *
 * <p>Permite categorizar y filtrar a los pacientes por situaciones
 * especificas como trabajo, familia, salud, entre otras.</p>
 */
@Entity
@Table(name = "situaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Situacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_situacion")
    private Long idSituacion;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relación inversa con paciente_situacion
    @OneToMany(mappedBy = "situacion", cascade = CascadeType.ALL)
    private List<PacienteSituacion> pacientesSituaciones;
}
