package com.amani.amaniapirest.dto.dtoPaciente.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida con los datos de una dirección postal de un paciente.
 *
 * <p>Incluye todos los campos de la dirección y el identificador del paciente
 * al que pertenece.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos DireccionResponseDTO")
public class DireccionResponseDTO {

    /** Nombre de la calle y número. */
    private String calle;

    /** Ciudad de residencia. */
    private String ciudad;

    /** Provincia o estado. */
    private String provincia;

    /** Código postal de la zona. */
    private String codigoPostal;

    /** País de residencia. */
    private String pais;
}
