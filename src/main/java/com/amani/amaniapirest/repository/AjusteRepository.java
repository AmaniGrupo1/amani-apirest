package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Ajuste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Ajuste}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consulta para obtener el ajuste por usuario.</p>
 */
public interface AjusteRepository extends JpaRepository<Ajuste, Long> {

    /**
     * Busca el ajuste asociado a un usuario especifico.
     *
     * @param idUsuario identificador del usuario
     * @return {@link Optional} con el ajuste del usuario, o vacio si no existe
     */
    Optional<Ajuste> findByUsuario_IdUsuario(Long idUsuario);

}
