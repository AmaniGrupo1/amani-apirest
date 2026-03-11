package com.amani.amaniapirest.dto.dtoAdmin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para que el administrador cree o actualice un registro
 * de {@code HistorialClinico}.
 *
 * <p>Vincula el registro al paciente correspondiente e incluye los campos
 * clínicos que el administrador puede gestionar directamente.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoAdminRequestDTO {

    /**
     * Identificador del {@code Paciente} al que pertenece este registro.
     * <p>El paciente debe existir previamente en el sistema.</p>
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

