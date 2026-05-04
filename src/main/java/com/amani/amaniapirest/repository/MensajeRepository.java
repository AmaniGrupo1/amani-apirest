package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Mensaje}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consultas para recuperar mensajes enviados o recibidos por usuario,
 * asi como el historial de una conversación entre dos usuarios.</p>
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

    /**
     * Recupera todos los mensajes de una conversación entre dos usuarios,
     * ordenados por fecha de envío ascendente.
     *
     * @param userId1 identificador del primer usuario
     * @param userId2 identificador del segundo usuario
     * @return lista de mensajes de la conversación ordenados por fecha
     */
    @Query("SELECT m FROM Mensaje m " +
            "WHERE (m.sender.idUsuario = :userId1 AND m.receiver.idUsuario = :userId2) " +
            "   OR (m.sender.idUsuario = :userId2 AND m.receiver.idUsuario = :userId1) " +
            "ORDER BY m.enviadoEn ASC")
    List<Mensaje> findConversationMessages(@Param("userId1") Long userId1,
                                            @Param("userId2") Long userId2);

    /**
     * Recupera los últimos {@code limit} mensajes de una conversación entre dos usuarios,
     * ordenados por fecha de envío descendente (los más recientes primero).
     *
     * @param userId1 identificador del primer usuario
     * @param userId2 identificador del segundo usuario
     * @param limit   número máximo de mensajes a devolver
     * @return lista de mensajes recientes de la conversación
     */
    @Query("SELECT m FROM Mensaje m " +
            "WHERE (m.sender.idUsuario = :userId1 AND m.receiver.idUsuario = :userId2) " +
            "   OR (m.sender.idUsuario = :userId2 AND m.receiver.idUsuario = :userId1) " +
            "ORDER BY m.enviadoEn DESC")
    List<Mensaje> findRecentConversationMessages(@Param("userId1") Long userId1,
                                                  @Param("userId2") Long userId2);
}
