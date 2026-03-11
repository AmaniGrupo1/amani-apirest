package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una direccion postal asociada a un paciente.
 *
 * <p>Un paciente puede tener multiples direcciones registradas, identificadas
 * cada una por su calle, ciudad, provincia, código postal, país y descripción.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "direcciones")
public class Direccion {

    /** Identificador unico de la direccion. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    /** Nombre de la calle y número. */
    private String calle;

    /** Ciudad de residencia. */
    private String ciudad;

    /** Provincia o estado. */
    private String provincia;

    /** Codigo postal de la zona. */
    private String codigoPostal;

    /** Pais de residencia. */
    private String pais;

    /** Descripción adicional o referencia de la ubicación. */
    private String descripcion;

    /** Paciente al que pertenece esta direccion. */
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
