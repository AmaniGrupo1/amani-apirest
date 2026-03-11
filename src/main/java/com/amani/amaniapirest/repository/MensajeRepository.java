package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Mensaje}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consultas para recuperar mensajes enviados o recibidos por usuario.</p>
 */
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    /**
     * Obtiene todos los mensajes enviados por un usuario.
     *
     * @param idUsuario identificador del usuario remitente
     * @return lista de mensajes enviados por el usuario
     */
    List<Mensaje> findBySender_IdUsuario(Long idUsuario);

    /**
     * Obtiene todos los mensajes recibidos por un usuario.
     *
     * @param idUsuario identificador del usuario destinatario
     * @return lista de mensajes recibidos por el usuario
     */
    List<Mensaje> findByReceiver_IdUsuario(Long idUsuario);
}

