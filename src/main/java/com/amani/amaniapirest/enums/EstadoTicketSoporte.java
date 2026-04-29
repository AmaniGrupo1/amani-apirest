package com.amani.amaniapirest.enums;

/**
 * Enumeracion de los estados posibles en el ciclo de vida de un ticket de soporte.
 *
 * <ul>
 *   <li>{@link #abierto}      - el ticket ha sido creado y esta pendiente de atencion.</li>
 *   <li>{@link #en_progreso}  - el ticket esta siendo atendido por el equipo de soporte.</li>
 *   <li>{@link #cerrado}      - el ticket fue resuelto o cerrado.</li>
 * </ul>
 */
public enum EstadoTicketSoporte {
    /** El ticket ha sido creado y esta pendiente de atencion. */
    abierto,
    /** El ticket esta siendo atendido por el equipo de soporte. */
    en_progreso,
    /** El ticket fue resuelto o cerrado. */
    cerrado
}
