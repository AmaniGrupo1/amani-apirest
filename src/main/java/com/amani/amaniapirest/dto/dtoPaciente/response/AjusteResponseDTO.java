package com.amani.amaniapirest.dto.dtoPaciente.response;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "AjusteResponse", description = "Preferencias de configuración de un usuario")
public class AjusteResponseDTO {

    @Schema(description = "Identificador único del ajuste", example = "1")
    private Long idAjuste;

    @Schema(description = "Identificador del usuario propietario", example = "1")
    private Long idUsuario;

    @Schema(description = "Idioma preferido del usuario", example = "es")
    private String idioma;

    @Schema(description = "Indica si las notificaciones están activadas", example = "true")
    private Boolean notificaciones;

    @Schema(description = "Indica si el modo oscuro está activado", example = "false")
    private Boolean darkMode;

    @Schema(description = "Zona horaria configurada", example = "Europe/Madrid")
    private String timezone;

    @Schema(description = "Fecha y hora de la última actualización", example = "2026-03-20T10:30:00")
    private LocalDateTime updatedAt;
}
