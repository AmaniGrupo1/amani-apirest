package com.amani.amaniapirest.enums;

/**
 * Enumeracion de los estados posibles en el ciclo de vida de una cita.
 *
 * <ul>
 *   <li>{@link #pendiente}  - la cita ha sido creada pero aún no confirmada.</li>
 *   <li>{@link #confirmada} - la cita fue aceptada por el psicólogo.</li>
 *   <li>{@link #cancelada}  - la cita fue cancelada por alguna de las partes.</li>
 *   <li>{@link #completada} - la cita se realizo satisfactoriamente.</li>
 * </ul>
 */
public enum EstadoCita {
    /** La cita ha sido creada y espera confirmación. */
    pendiente,
    /** La cita fue confirmada por el psicólogo. */
    confirmada,
    /** La cita fue cancelada antes de realizarse. */
    cancelada,
    /** La cita se completó exitosamente. */
    completada
}
