package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import com.amani.amaniapirest.models.DocumentoLegal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la gestión de documentos legales de la plataforma
 * (términos y condiciones, política de privacidad, aviso legal, etc.).
 *
 * <p>Los documentos legales están versionados y se puede activar o desactivar cada versión.
 * Extiende {@link JpaRepository} para proveer operaciones CRUD estándar.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface DocumentoLegalRepository extends JpaRepository<DocumentoLegal, Long> {

    /**
     * Recupera todos los documentos legales de un tipo concreto que se encuentren activos,
     * ordenados por su campo de posición de visualización de forma ascendente.
     *
     * <p>Se utiliza para mostrar al usuario la lista ordenada de versiones activas de un
     * tipo de documento (por ejemplo, todas las versiones activas de la política de privacidad).</p>
     *
     * @param tipo Tipo de documento legal a filtrar ({@link TipoDocumentoLegal}).
     * @return Lista de {@link DocumentoLegal} activos del tipo indicado, ordenados por su posición de visualización.
     */
    List<DocumentoLegal> findByTipoAndActivoTrueOrderByOrdenVisualizacionAsc(
            TipoDocumentoLegal tipo
    );

    /**
     * Recupera el documento legal activo de un tipo concreto.
     *
     * <p>Garantiza que se obtiene el documento vigente de un tipo específico. Se espera que
     * exista como máximo un documento activo por tipo en cada momento.</p>
     *
     * @param tipo Tipo de documento legal ({@link TipoDocumentoLegal}).
     * @return {@link Optional} con el documento legal activo del tipo indicado,
     *         o vacío si no existe ninguno activo para ese tipo.
     */
    Optional<DocumentoLegal> findByTipoAndActivoTrue(
            TipoDocumentoLegal tipo
    );
}
