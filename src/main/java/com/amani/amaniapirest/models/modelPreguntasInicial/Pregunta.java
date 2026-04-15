package com.amani.amaniapirest.models.modelPreguntasInicial;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una pregunta del cuestionario inicial de onboarding del paciente.
 *
 * <p>Cada pregunta puede tener multiples opciones de respuesta predefinidas
 * y es utilizada para recopilar informacion basica sobre el paciente al
 * momento de su registro en el sistema.</p>
 *
 * @see Opcion
 * @see Respuesta
 */
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
        private List<Opcion> opciones = new ArrayList<>();
}
