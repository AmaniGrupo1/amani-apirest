package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que almacena las preferencias de configuración personal de un usuario.
 *
 * <p>Cada usuario puede tener un unico registro de ajustes con preferencias
 * de idioma, notificaciones, modo oscuro y zona horaria.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ajustes")
public class Ajuste {

    /** Identificador unico del ajuste. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAjuste;

    /** Idioma preferido por el usuario (p.ej. "es", "en"). */
    private String idioma;

    /** Indica si el usuario tiene activadas las notificaciones. */
    private boolean notificaciones;

    /** Indica si el usuario tiene activado el modo oscuro. */
    private boolean darkMode;

    /** Zona horaria preferida del usuario (p.ej. "Europe/Madrid"). */
    private String timezone;

    /** Fecha y hora de la ultima actualización de los ajustes. */
    private LocalDateTime updatedAt;

    /** Usuario propietario de estos ajustes. */
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
