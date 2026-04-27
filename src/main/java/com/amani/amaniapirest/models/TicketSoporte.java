package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.CategoriaTicketSoporte;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.enums.TipoTicketSoporte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un ticket de soporte creado por un usuario.
 *
 * <p>Cada ticket tiene tipo, categoria, titulo, descripcion y un estado
 * que evoluciona a lo largo de su ciclo de vida.</p>
 */
@Entity
@Getter
@Setter
@ToString(exclude = {"usuario"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tickets_soporte")
public class TicketSoporte {

    /** Identificador unico del ticket. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Long idTicket;

    /** Titulo breve del ticket. */
    @Column(nullable = false, length = 200)
    private String titulo;

    /** Descripcion detallada del problema o consulta. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /** Tipo del ticket segun {@link TipoTicketSoporte}. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoTicketSoporte tipo;

    /** Categoria del ticket segun {@link CategoriaTicketSoporte}. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaTicketSoporte categoria;

    /** Estado actual del ticket segun {@link EstadoTicketSoporte}. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private EstadoTicketSoporte estado = EstadoTicketSoporte.abierto;

    /** Fecha y hora de creacion del ticket. */
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    /** Fecha y hora de la ultima actualizacion del ticket. */
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    /** Fecha y hora de cierre del ticket, o {@code null} si sigue abierto. */
    @Column(name = "cerrado_en")
    private LocalDateTime cerradoEn;

    /** Usuario que creo el ticket. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoTicketSoporte.abierto;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
        if (this.estado == EstadoTicketSoporte.cerrado && this.cerradoEn == null) {
            this.cerradoEn = LocalDateTime.now();
        }
    }
}
