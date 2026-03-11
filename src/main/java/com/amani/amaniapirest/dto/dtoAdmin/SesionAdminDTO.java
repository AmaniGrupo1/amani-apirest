package com.amani.amaniapirest.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionAdminDTO {
    private Long idSesion;
    private Long idCita;
    private Long idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private Long idPsicologo;
    private String nombrePsicologo;
    private String apellidoPsicologo;
    private LocalDateTime sessionDate;
    private Integer durationMinutes;
    private String notas;
    private String recomendaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
