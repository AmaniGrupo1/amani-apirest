package com.amani.amaniapirest.dto.dtoPsicologo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para que el psicólogo cree o actualice un registro
 * de {@code HistorialClinico} de uno de sus pacientes.
 *
 * <p>Solo el psicólogo que atiende al paciente puede registrar entradas
 * en su historial clínico.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoPsicologoRequestDTO {

    /**
     * Identificador del {@code Paciente} al que pertenece este registro.
     * <p>El paciente debe estar vinculado a una cita del psicólogo autenticado.</p>
     */
    private Long idPaciente;

    /** Título o encabezado del registro clínico. */
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    private String diagnostico;

    /** Descripción del tratamiento prescrito o aplicado. */
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    private String observaciones;
}

