package com.amani.amaniapirest.dto.dtoPsicologo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para que el psicólogo consulte las entradas del diario emocional de un paciente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiarioEmocionalPsicologoDTO {

    /** Identificador de la entrada en el diario emocional */
    private Long idDiario;

    /** Identificador del paciente */
    private Long idPaciente;

    /** Nombre del paciente */
    private String nombrePaciente;

    /** Apellido del paciente */
    private String apellidoPaciente;

    /** Fecha y hora del registro de la emoción */
    private LocalDateTime fecha;

    /** Nombre de la emoción */
    private String emocion;

    /** Intensidad de la emoción (1-10) */
    private Integer intensidad;

    /** Nota o comentario adicional */
    private String nota;
}
