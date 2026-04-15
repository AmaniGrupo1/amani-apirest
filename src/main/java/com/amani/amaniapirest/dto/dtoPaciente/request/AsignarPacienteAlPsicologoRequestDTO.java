package com.amani.amaniapirest.dto.dtoPaciente.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de asignación de un paciente a un psicólogo.
 *
 * <p>Utilizado cuando un administrador o coordinador asigna un paciente
 * a un psicólogo específico para la atención de su tratamiento.</p>
 *
 * @param idPaciente   identificador único del paciente
 * @param idPsicologo  identificador único del psicólogo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignarPacienteAlPsicologoRequestDTO {
    private Long idPaciente;
    private Long idPsicologo;
}
