package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida con las preferencias de configuración de un usuario.
 *
 * <p>Expone los ajustes guardados sin incluir la entidad completa del usuario.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjusteResponseDTO {

    /** Identificador único del ajuste. */
    private Long idAjuste;

    /** Identificador del usuario propietario de estos ajustes. */
    private Long idUsuario;

    /** Idioma preferido del usuario. */
    private String idioma;

    /** Indica si las notificaciones están activadas. */
    private Boolean notificaciones;

    /** Indica si el modo oscuro está activado. */
    private Boolean darkMode;

    /** Zona horaria configurada por el usuario. */
    private String timezone;

    /** Fecha y hora de la última actualización de los ajustes. */
    private LocalDateTime updatedAt;
}
