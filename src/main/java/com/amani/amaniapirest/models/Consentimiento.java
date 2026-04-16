package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa el consentimiento informado de un paciente.
 *
 * <p>Almacena la fecha de aceptacion, la version del documento y las
 * preferencias del paciente respecto a videoconferencias y comunicacion.</p>
 */
@Entity
@Table(name = "consentimientos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Consentimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsentimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion = LocalDateTime.now();

    @Column(name = "version_documento", nullable = false, length = 20)
    private String versionDocumento;

    @Column(name = "acepta_terminos", nullable = false)
    private Boolean aceptaTerminos = false;

    @Column(name = "acepta_videoconferencia")
    private Boolean aceptaVideoconferencia = false;

    @Column(name = "acepta_comunicacion")
    private Boolean aceptaComunicacion = false;
}
