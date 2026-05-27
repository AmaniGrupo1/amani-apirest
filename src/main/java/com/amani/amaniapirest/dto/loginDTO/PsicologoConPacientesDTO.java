package com.amani.amaniapirest.dto.loginDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para representar un psicólogo con la lista de sus pacientes asignados.
 *
 * <p>Incluye la información completa del psicólogo y una lista de los pacientes
 * bajo su responsabilidad clínica.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos PsicologoConPacientesDTO")
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