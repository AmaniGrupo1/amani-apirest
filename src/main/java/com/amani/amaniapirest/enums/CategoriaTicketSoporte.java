package com.amani.amaniapirest.enums;

/**
 * Enumeracion de las categorias posibles para un ticket de soporte.
 *
 * <ul>
 *   <li>{@link #tecnico} - problemas tecnicos, errores o bugs.</li>
 *   <li>{@link #cuenta}  - gestion de perfil, datos personales o acceso.</li>
 *   <li>{@link #pago}    - consultas relacionadas con pagos y facturacion.</li>
 *   <li>{@link #app}     - funcionamiento de la aplicacion movil.</li>
 *   <li>{@link #otro}    - cualquier otra categoria no contemplada.</li>
 * </ul>
 */
public enum CategoriaTicketSoporte {
    /** Problemas tecnicos, errores o bugs. */
    tecnico,
    /** Gestion de perfil, datos personales o acceso. */
    cuenta,
    /** Consultas relacionadas con pagos y facturacion. */
    pago,
    /** Funcionamiento de la aplicacion movil. */
    app,
    /** Cualquier otra categoria no contemplada. */
    otro
}
