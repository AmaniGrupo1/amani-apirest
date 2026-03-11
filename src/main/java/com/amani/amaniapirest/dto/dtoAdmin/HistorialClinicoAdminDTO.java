package com.amani.amaniapirest.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoAdminDTO {
    private Long idHistory;
    private Long idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String titulo;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime creadoEn;
}
