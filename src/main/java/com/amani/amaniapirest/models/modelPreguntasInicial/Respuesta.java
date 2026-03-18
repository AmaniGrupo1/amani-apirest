package com.amani.amaniapirest.models.modelPreguntasInicial;

import com.amani.amaniapirest.models.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que registra la respuesta de un paciente a una pregunta del cuestionario inicial.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "respuestas")
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRespuesta;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_pregunta")
    private Pregunta pregunta;

    @ManyToOne
    @JoinColumn(name = "id_opcion")
    private Opcion opcion;

    private String texto;

    private LocalDateTime creadoEn;
}
