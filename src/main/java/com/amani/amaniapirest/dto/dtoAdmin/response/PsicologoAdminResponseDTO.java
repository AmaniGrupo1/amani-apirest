package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta con los datos de un psicologo y su paciente asociado
 * para la vista de administracion.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PsicologoAdminResponse", description = "Psicólogo con paciente asociado — vista administrador")
public class PsicologoAdminResponseDTO {
    /** Nombre del psicologo. */
    @Schema(description = "Nombre del psicólogo", example = "Ana")

    private String nombrePsicologo;
    /** Apellido del psicologo. */
    @Schema(description = "Apellido del psicólogo", example = "García")

    private String apellidoPsicologo;
    /** Nombre del usuario (paciente) asociado. */
    @Schema(description = "Nombre del paciente asociado", example = "Carlos")

    private String nombreUsuario;
    /** Apellido del usuario (paciente) asociado. */
    @Schema(description = "Apellido del paciente asociado", example = "López")

    private String apellidoUsuario;
    /** Email del usuario asociado. */
    @Schema(description = "Email del paciente asociado", example = "carlos@amani.com")

    private String emailUsuario;
    /** Fecha de ultima actualizacion del registro. */
    @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

    private LocalDateTime updatedAt;
}
