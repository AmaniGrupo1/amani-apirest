package com.amani.amaniapirest.dto.dtoAdmin.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiarioEmocionAdminResponseDTO {

    /** Nombre del paciente */
    private String nombrePaciente;

    /** Apellido del paciente */
    private String apellidoPaciente;

    /** Fecha y hora del registro */
    private LocalDateTime fecha;

    /** Emoción registrada */
    private String emocion;

    /** Intensidad de la emoción (1-10) */
    private Integer intensidad;

    /** Nota o comentario del paciente */
    private String nota;

    /** Fecha y hora de creación del registro (auditoría) */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización (auditoría) */
    private LocalDateTime updatedAt;
}