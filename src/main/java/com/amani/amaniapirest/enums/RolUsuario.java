package com.amani.amaniapirest.enums;

/**
 * Enumeracion de los roles funcionales disponibles para los usuarios del sistema.
 *
 * <ul>
 *   <li>{@link #admin}     - acceso completo de administracion.</li>
 *   <li>{@link #psicologo} - profesional que atiende citas y sesiones.</li>
 *   <li>{@link #paciente}  - usuario que solicita y asiste a citas.</li>
 * </ul>
 */
public enum RolUsuario {
    /** Administrador con acceso total al sistema. */
    admin,
    /** Psicologo que gestiona citas y sesiones terapeuticas. */
    psicologo,
    /** Paciente que recibe atencion psicologica. */
    paciente
}
