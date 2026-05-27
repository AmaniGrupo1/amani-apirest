package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la gestión de archivos adjuntos vinculados a sesiones terapéuticas.
 *
 * <p>Los archivos pueden incluir documentos, informes o recursos multimedia que el psicólogo
 * o el paciente comparten durante o después de una sesión. Extiende {@link JpaRepository}
 * para proveer operaciones CRUD estándar.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

    /**
     * Recupera todos los archivos adjuntos pertenecientes a una sesión terapéutica específica.
     *
     * <p>Permite listar el material compartido dentro del contexto de una sesión concreta.</p>
     *
     * @param idSesion Identificador único de la sesión terapéutica.
     * @return Lista de {@link Archivo} asociados a la sesión indicada; lista vacía si no hay archivos.
     */
    List<Archivo> findBySesion_IdSesion(Long idSesion);
}


