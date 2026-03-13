package com.amani.amaniapirest.models.modelPreguntasInicial;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "opciones")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpcion;

    private String texto;
    private Integer valor;

    @ManyToOne
    @JoinColumn(name = "id_pregunta")
    private Pregunta pregunta;

}
