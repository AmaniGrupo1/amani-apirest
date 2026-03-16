package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre un {@code HistorialClinico}.
 *
 * <p>Expone el registro clínico completo incluyendo los datos de identificación
 * del paciente vinculado, el diagnóstico, el tratamiento y las observaciones
 * del profesional.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoAdminResponseDTO {

    /** Nombre de pila del paciente. */
    private String nombrePaciente;

    /** Apellido del paciente. */
    private String apellidoPaciente;

    /** Título o encabezado del registro clínico. */
    private String titulo;

    /** Diagnóstico emitido por el psicólogo. */
    private String diagnostico;

    /** Tratamiento prescrito o aplicado. */
    private String tratamiento;

    /** Observaciones adicionales del profesional. */
    private String observaciones;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime creadoEn;
}

