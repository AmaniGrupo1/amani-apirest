package com.amani.amaniapirest.dto.dtoPaciente.response;


import com.amani.amaniapirest.dto.dtoAdmin.TutorResonseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PacienteBasicoResponse", description = "Datos personales del paciente sin pagos ni info clínica avanzada")
public class PacienteBasicoResponseDTO {

    private Long idPaciente;

    private Long idUsuario;

    private String nombre;
    private String apellido;
    private String email;
    private String dni;

    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;

    /** Lista de direcciones del paciente */
    private List<DireccionResponseDTO> direcciones;

    /** Lista de tutores del paciente */
    private List<TutorResonseDTO> tutores;
}
