package com.amani.amaniapirest.dto.dtoPaciente.response;

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

    /** Descripción adicional o referencia de la ubicación. */
    private String descripcion;
}
