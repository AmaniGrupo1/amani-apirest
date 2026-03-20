package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para registrar o actualizar una dirección de un paciente.
 *
 * <p>Los campos {@code idPaciente} y {@code calle} son obligatorios.
 * El resto de campos son opcionales.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DireccionRequest", description = "Datos para registrar o actualizar una dirección de un paciente")
public class DireccionRequestDTO {

    /** Identificador del paciente al que pertenece la dirección. */
    @NotNull
    @Schema(description = "Identificador del paciente", example = "1")
    private Long idPaciente;

    /** Nombre de la calle y número. */
    @NotBlank
    @Schema(description = "Nombre de la calle y número", example = "Calle Mayor 12")
    private String calle;

    /** Ciudad de residencia. */
    @Schema(description = "Ciudad de residencia", example = "Madrid")
    private String ciudad;

    /** Provincia o estado. */
    @Schema(description = "Provincia o estado", example = "Madrid")
    private String provincia;

    /** Código postal de la zona. */
    @Schema(description = "Código postal", example = "28001")
    private String codigoPostal;

    /** País de residencia. */
    @Schema(description = "País de residencia", example = "España")
    private String pais;

    /** Descripción adicional o referencia de la ubicación. */
    @Schema(description = "Descripción adicional o referencia", example = "Puerta 3B, escalera izquierda")
    private String descripcion;
}
