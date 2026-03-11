package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear o actualizar los ajustes de configuración de un usuario.
 *
 * <p>El campo {@code idUsuario} es obligatorio. El resto de preferencias son
 * opcionales y tienen valores por defecto razonables en el servicio.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjusteRequestDTO {

    /** Identificador del usuario al que pertenecen estos ajustes. */
    @NotNull
    private Long idUsuario;

    /** Idioma preferido (p.ej. "es", "en"). */
    private String idioma;

    /** Indica si el usuario desea recibir notificaciones. */
    private Boolean notificaciones;

    /** Indica si el usuario prefiere el modo oscuro. */
    private Boolean darkMode;

    /** Zona horaria preferida (p.ej. "Europe/Madrid"). */
    private String timezone;
}
