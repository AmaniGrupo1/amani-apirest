package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notificación push enviada a un usuario de la plataforma.
 *
 * <p>Registra el título, el contenido del mensaje y el estado de lectura de cada
 * notificación. Se envía a través de Firebase Cloud Messaging (FCM) utilizando
 * el token registrado en {@link com.amani.amaniapirest.models.Usuario#fcmToken}.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    /** Identificador único de la notificación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    /** Usuario destinatario de la notificación. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /** Título breve de la notificación, visible en el banner del dispositivo. */
    @Column(name = "titulo")
    private String titulo;

    /** Cuerpo del mensaje de la notificación. */
    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    /** Indica si el usuario ya ha visualizado la notificación. */
    @Column(name = "leida")
    private Boolean leida = false;

    /** Fecha y hora en que se generó la notificación. */
    @Column(name = "creada_en")
    private LocalDateTime creadaEn = LocalDateTime.now();
}