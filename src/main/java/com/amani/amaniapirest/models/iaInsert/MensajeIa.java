package com.amani.amaniapirest.models.iaInsert;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_ia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_conversacion", nullable = false)
    private ConversacionIa conversacion;

    @Column(nullable = false)
    private String rol; // system, user, assistant

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    // getters y setters
}