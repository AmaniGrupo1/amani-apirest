package com.amani.amaniapirest.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para representar un tutor o responsable legal de un paciente.
 *
 * <p>Contiene los datos de contacto y de identificación del tutor,
 * utilizado principalmente para pacientes menores de edad o bajo tutela.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TutorResonseDTO {
    private String nombre;
    private String telefono;
    private String email;
    private String dni;
    private String tipo;
}
