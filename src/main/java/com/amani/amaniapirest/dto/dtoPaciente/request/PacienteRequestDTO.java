package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de entrada para crear o actualizar el perfil completo de un paciente.
 *
 * <p>Permite enviar en una sola petición los datos demográficos del paciente,
 * los datos del usuario vinculado, sus direcciones, citas, historial clínico
 * y respuestas al cuestionario inicial. Algunos campos son exclusivos del
 * rol administrador (citas, historiales).</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteRequestDTO {

    /** Identificador de un {@code Usuario} existente; solo lo usa el administrador para vincular perfiles. */
    private Long idUsuario;

    /** Fecha de nacimiento del paciente. */
    private LocalDate fechaNacimiento;

    /** Género del paciente. */
    private String genero;

    /** Número de teléfono del paciente. */
    private String telefono;

    /** Datos del nuevo usuario a crear junto con el perfil de paciente. */
    private UsuarioRequestDTO usuario;

    /** Direcciones postales del paciente. */
    private List<DireccionRequestDTO> direcciones;

    /** Citas del paciente; uso exclusivo del administrador. */
    private List<CitaRequestDTO> citas;

    /** Registros del historial clínico; accesibles por administrador y psicólogo. */
    private List<HistorialClinicoRequestDTO> historiales;

    /** Respuestas del paciente al cuestionario de evaluación inicial. */
    private List<RespuestasRequestDTO> respuestas;
}
