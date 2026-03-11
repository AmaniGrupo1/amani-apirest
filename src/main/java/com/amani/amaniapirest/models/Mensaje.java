package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje intercambiado entre dos usuarios.
 *
 * <p>Los mensajes pueden estar asociados a una cita específica, tienen
 * un remitente ({@code sender}) y un destinatario ({@code receiver}),
 * e indican si ya fueron leidos por el destinatario.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mensajes")
public class Mensaje {

    /** Identificador unico del mensaje. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    /** Identificador de la cita relacionada con el mensaje, si aplica. */
    private Long idCita;

    /** Contenido textual del mensaje. */
    private String mensaje;

    /** Fecha y hora en que el mensaje fue enviado. */
    private LocalDateTime enviadoEn;

    /** Indica si el mensaje fue leído por el destinatario. */
    private boolean leido;

    /** Usuario que envia el mensaje. */
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Usuario sender;

    /** Usuario que recibe el mensaje. */
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Usuario receiver;
}
