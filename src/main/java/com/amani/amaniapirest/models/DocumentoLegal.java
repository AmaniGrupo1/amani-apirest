package com.amani.amaniapirest.models;


import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entidad que almacena los documentos legales gestionados por el administrador de la plataforma.
 *
 * <p>Incluye documentos como políticas de privacidad, términos y condiciones y aviso legal.
 * Cada documento está versionado, puede activarse o desactivarse, y lleva un orden de
 * visualización para su presentación al usuario. El tipo se discrimina mediante
 * {@link TipoDocumentoLegal}.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Entity
@Table(name = "documentos_legales", schema = "psicologia_app")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoLegal {

    /** Identificador único del documento legal. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocumento;

    /** Tipo de documento según {@link TipoDocumentoLegal} (p.ej. POLITICA_PRIVACIDAD, TERMINOS). */
    @Column(name = "tipo", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TipoDocumentoLegal tipo;

    /** Título visible del documento en la interfaz de usuario. */
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    /** Contenido completo del documento en formato texto plano o HTML. */
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    /** Nombre del icono asociado al documento para su representación visual. */
    @Column(name = "icono", length = 100)
    private String icono;

    /** Posición del documento en la lista de visualización (menor valor, mayor prioridad). */
    @Column(name = "orden_visualizacion", nullable = false)
    private Integer ordenVisualizacion = 0;

    /** Versión del documento (p.ej. "1.0", "2.3"). Facilita el control de cambios legales. */
    @Column(name = "version", length = 20)
    private String version;

    /** Indica si el documento está vigente y visible para los usuarios de la plataforma. */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /** Fecha y hora en que el documento fue creado. */
    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    /** Fecha y hora de la última modificación del documento. */
    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    /**
     * Inicializa las fechas de auditoría al persistir el documento por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    /**
     * Actualiza la fecha de modificación cada vez que el documento es actualizado.
     */
    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
