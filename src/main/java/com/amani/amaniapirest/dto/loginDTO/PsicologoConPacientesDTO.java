package com.amani.amaniapirest.dto.loginDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoConPacientesDTO {

    private Long idPsicologo;           // Id del psicólogo
    private String nombrePsicologo;
    private String apellidoPsicologo;
    private String emailPsicologo;
    private String especialidad;
    private String licencia;
    private LocalDateTime fechaDadoAlta;

    private List<PacientesAsignadoDTO> pacientes;  // Lista de pacientes asignados
}