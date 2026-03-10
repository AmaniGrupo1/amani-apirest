package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archivos")
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArchivo;
    private String nombre;
    private String tipoMime;
    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] datos;
    private LocalDateTime creadoEn;
    @ManyToOne
    @JoinColumn(name = "id_sesion")
    private Sesion sesion;
}