package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.TicketSoporte;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link TicketSoporte}.
 */
@Repository
public interface TicketSoporteRepository extends JpaRepository<TicketSoporte, Long> {

    /**
     * Busca todos los tickets de un usuario ordenados por fecha de creacion descendente.
     *
     * @param idUsuario identificador del usuario
     * @return lista de tickets ordenados del mas reciente al mas antiguo
     */
    List<TicketSoporte> findByUsuario_IdUsuarioOrderByCreadoEnDesc(Long idUsuario);

    /**
     * Busca los tickets de un usuario filtrados por estado.
     *
     * @param idUsuario identificador del usuario
     * @param estado    estado del ticket
     * @return lista de tickets filtrados y ordenados
     */
    List<TicketSoporte> findByUsuario_IdUsuarioAndEstadoOrderByCreadoEnDesc(Long idUsuario, EstadoTicketSoporte estado);
}
