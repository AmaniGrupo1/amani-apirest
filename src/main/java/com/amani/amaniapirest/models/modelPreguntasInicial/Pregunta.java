package com.amani.amaniapirest.models.modelPreguntasInicial;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "preguntas")
public class Pregunta {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idPregunta;

        private String texto;
        private String tipo;

        private LocalDateTime creadoEn;

        @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Opcion> opciones;
}
