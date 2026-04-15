package com.amani.amaniapirest.dto.loginDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para representar un paciente asignado a un psicólogo.
 *
 * <p>Devuelve los datos básicos del paciente (identificador, nombre, apellido y email)
 * cuando un psicólogo consulta la lista de sus pacientes asignados.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientesAsignadoDTO {
    private Long idPaciente;        // Id del paciente
    private String nombre;          // Nombre del paciente
    private String apellido;        // Apellido del paciente
    private String email;           // Email del paciente
}
