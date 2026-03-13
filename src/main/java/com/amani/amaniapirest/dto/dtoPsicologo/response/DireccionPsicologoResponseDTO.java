package com.amani.amaniapirest.dto.dtoPsicologo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionPsicologoResponseDTO {

    /** Nombre de pila del paciente. */
    private String nombrePaciente;

    /** Apellido del paciente. */
    private String apellidoPaciente;

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
