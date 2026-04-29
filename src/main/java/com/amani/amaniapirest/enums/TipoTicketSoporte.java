package com.amani.amaniapirest.enums;

/**
 * Enumeracion de los tipos posibles para un ticket de soporte.
 *
 * <ul>
 *   <li>{@link #problema}  - reporte de un fallo o comportamiento inesperado.</li>
 *   <li>{@link #pregunta}  - consulta sobre funcionamiento del sistema.</li>
 *   <li>{@link #sugerencia} - idea de mejora o nueva funcionalidad.</li>
 * </ul>
 */
public enum TipoTicketSoporte {
    /** Reporte de un fallo o comportamiento inesperado. */
    problema,
    /** Consulta sobre funcionamiento del sistema. */
    pregunta,
    /** Idea de mejora o nueva funcionalidad. */
    sugerencia
}
