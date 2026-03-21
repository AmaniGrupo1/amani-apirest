package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "AjusteRequest", description = "Datos para crear o actualizar ajustes de configuración de usuario")
public class AjusteRequestDTO {

    /** Identificador del usuario al que pertenecen estos ajustes. */
    @NotNull
    @Schema(description = "Identificador del usuario propietario", example = "1")
    private Long idUsuario;

    /** Idioma preferido (p.ej. "es", "en"). */
    @Schema(description = "Idioma preferido (p.ej. 'es', 'en')", example = "es")
    private String idioma;

    /** Indica si el usuario desea recibir notificaciones. */
    @Schema(description = "Indica si el usuario desea recibir notificaciones", example = "true")
    private Boolean notificaciones;

    /** Indica si el usuario prefiere el modo oscuro. */
    @Schema(description = "Indica si el usuario prefiere el modo oscuro", example = "false")
    private Boolean darkMode;

    /** Zona horaria preferida (p.ej. "Europe/Madrid"). */
    @Schema(description = "Zona horaria preferida", example = "Europe/Madrid")
    private String timezone;
}
