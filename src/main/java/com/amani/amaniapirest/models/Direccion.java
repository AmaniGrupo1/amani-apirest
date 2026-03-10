package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "direcciones")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;
    private String calle;
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private String pais;
    private String descripcion;
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;
}
