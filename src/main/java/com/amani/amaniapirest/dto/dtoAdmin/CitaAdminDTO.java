package com.amani.amaniapirest.dto.dtoAdmin;

import com.amani.amaniapirest.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaAdminDTO {
    private Long idCita;
    private Long idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private Long idPsicologo;
    private String nombrePsicologo;
    private String apellidoPsicologo;
    private LocalDateTime startDatetime;
    private Integer durationMinutes;
    private EstadoCita estadoCita;
    private String motivo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
