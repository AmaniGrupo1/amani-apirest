package com.amani.amaniapirest.dto.dtoPsicologo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para que el psicólogo consulte la dirección postal de uno de sus pacientes.
 *
 * <p>Incluye el nombre completo del paciente y todos los campos de la dirección.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DireccionPsicologoResponse", description = "Dirección de un paciente — vista psicólogo")
public class DireccionPsicologoResponseDTO {

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")

    private String nombrePaciente;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")

    private String apellidoPaciente;

    /** Nombre de la calle y número. */
    @Schema(description = "Calle y número", example = "Calle Mayor 12")

    private String calle;

    /** Ciudad de residencia. */
    @Schema(description = "Ciudad", example = "Madrid")

    private String ciudad;

    /** Provincia o estado. */
    @Schema(description = "Provincia", example = "Madrid")

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
