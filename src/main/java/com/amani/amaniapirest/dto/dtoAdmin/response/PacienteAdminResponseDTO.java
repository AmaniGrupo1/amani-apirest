package com.amani.amaniapirest.dto.dtoAdmin.response;

import com.amani.amaniapirest.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * DTO de respuesta con los datos completos de un paciente para la vista de administracion.
 *
 * <p>Incluye datos del usuario asociado (nombre, apellido, email, estado activo)
 * junto con la informacion clinica del paciente.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PacienteAdminResponse", description = "Paciente completo — vista administrador")
public class PacienteAdminResponseDTO {
    /** Identificador unico del paciente. */
    @Schema(description = "Identificador del paciente", example = "1")

    private Long idPaciente;
    /** Nombre del usuario asociado. */
    @Schema(description = "Nombre del usuario", example = "Laura")

    private String nombreUsuario;
    /** Apellido del usuario asociado. */
    @Schema(description = "Apellido del usuario", example = "Martínez")

    private String apellidoUsuario;
    /** Email del usuario asociado. */
    @Schema(description = "Email del usuario", example = "laura@amani.com")

    private String emailUsuario;
    /** Fecha de nacimiento del paciente. */
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")

    private LocalDate fechaNacimiento;
    /** Genero del paciente. */
    @Schema(description = "Género", example = "femenino")

    private String genero;
    /** Telefono de contacto. */
    @Schema(description = "Teléfono", example = "+34612345678")

    private String telefono;
    /** Fecha de creacion del perfil. */
    @Schema(description = "Fecha de creación", example = "2026-01-15T08:00:00")

    private LocalDateTime createdAt;
    /** Fecha de ultima actualizacion. */
    @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

    private LocalDateTime updatedAt;
    /** Indica si la cuenta esta activa. */
    @Schema(description = "Si la cuenta está activa", example = "true")

    private Boolean activo;
}
