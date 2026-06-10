package com.amani.amaniapirest.models.iaInsert;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversaciones_ia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversacionIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversacion")
    private Long idConversacion;

    @Column(name = "id_paciente", nullable = false)
    private Long idPaciente;

    private String titulo;

    @Column(name = "creada_en")
    private LocalDateTime creadaEn = LocalDateTime.now();

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensajeIa> mensajes;

    // getters y setters
}
