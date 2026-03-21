package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida con los datos de una dirección postal de un paciente.
 *
 * <p>Incluye todos los campos de la dirección y el identificador del paciente
 * al que pertenece.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DireccionResponse", description = "Dirección postal de un paciente")
public class DireccionResponseDTO {

    /** Nombre de la calle y número. */
    @Schema(description = "Calle y número", example = "Calle Mayor 12")

    private String calle;

    /** Ciudad de residencia. */
    @Schema(description = "Ciudad", example = "Madrid")

    private String ciudad;

    /** Provincia o estado. */
    @Schema(description = "Provincia o estado", example = "Madrid")

    private String provincia;

    /** Código postal de la zona. */
    @Schema(description = "Código postal", example = "28001")

    private String codigoPostal;

    /** País de residencia. */
    @Schema(description = "País", example = "España")

    private String pais;

    /** Descripción adicional o referencia de la ubicación. */
    @Schema(description = "Descripción adicional", example = "Puerta 3B")

    private String descripcion;
}
