package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un archivo adjunto asociado a una sesion terapéutica.
 *
 * <p>Almacena el contenido binario del archivo junto con su nombre, tipo MIME
 * y la fecha de creación. Cada archivo pertenece a una {@link Sesion}.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archivos")
public class Archivo {

    /** Identificador unico del archivo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArchivo;

    /** Nombre original del archivo incluyendo extension. */
    private String nombre;

    /** Tipo MIME del archivo (p.ej. "application/pdf", "image/png"). */
    private String tipoMime;

    /** Contenido binario del archivo almacenado como bytea en la base de datos. */
    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] datos;

    /** Fecha y hora en que el archivo fue creado o subido. */
    private LocalDateTime creadoEn;

    /** Sesion a la que pertenece este archivo. */
    @ManyToOne
    @JoinColumn(name = "id_sesion")
    private Sesion sesion;
}