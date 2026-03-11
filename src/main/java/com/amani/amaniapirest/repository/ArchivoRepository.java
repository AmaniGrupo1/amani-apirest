package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Archivo}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consulta para listar archivos por sesion.</p>
 */
public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

    /**
     * Obtiene todos los archivos asociados a una sesion especifica.
     *
     * @param idSesion identificador de la sesion
     * @return lista de archivos pertenecientes a la sesion
     */
    List<Archivo> findBySesion_IdSesion(Long idSesion);
}


