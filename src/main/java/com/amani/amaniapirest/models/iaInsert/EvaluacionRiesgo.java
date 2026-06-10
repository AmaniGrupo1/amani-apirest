package com.amani.amaniapirest.models.iaInsert;


import com.amani.amaniapirest.enums.NivelRiesgo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones_riesgo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_riesgo")
    private NivelRiesgo nivelRiesgo;

    private BigDecimal score;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

}
