package com.amani.amaniapirest.dto.dtoPaciente.request;
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
public class DireccionRequestDTO {

    /** Identificador del paciente al que pertenece la dirección. */
    @NotNull
    private Long idPaciente;

    /** Nombre de la calle y número. */
    @NotBlank
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
