package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de entrada para crear o actualizar un paciente.
 *
 * <p>Contiene los datos clinicos del paciente (fecha de nacimiento, genero, telefono),
 * el usuario asociado y opcionalmente listas de direcciones, citas, historiales
 * y respuestas al test inicial.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PacienteRequest", description = "Datos para crear o actualizar un paciente")
public class PacienteRequestDTO {
    @Schema(description = "ID del usuario existente (para asociar)", example = "1")
    private Long idUsuario;
    @Schema(description = "Fecha de nacimiento del paciente", example = "1990-05-15")
    private LocalDate fechaNacimiento;
    @Schema(description = "Género del paciente", example = "femenino")
    private String genero;
    @Schema(description = "Teléfono de contacto", example = "+34612345678")
    private String telefono;
    @Schema(description = "Datos del usuario a crear junto al paciente")
    private UsuarioRequestDTO usuario;
    @Schema(description = "Direcciones del paciente (opcional)")
    private List<DireccionRequestDTO> direcciones;
    @Schema(description = "Citas asociadas (solo para admin)")
    private List<CitaRequestDTO> citas;
    @Schema(description = "Historiales clínicos (admin y psicólogo)")
    private List<HistorialClinicoRequestDTO> historiales;
    @Schema(description = "Respuestas al test inicial (opcional)")
    private List<RespuestasRequestDTO> respuestas;
}
