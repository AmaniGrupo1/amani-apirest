package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;
import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Respuesta}.
 *
 * <p>Incluye queries personalizadas para proyectar respuestas como DTOs
 * y para cargar eagerly las relaciones con pregunta, opcion y paciente.</p>
 */
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    /** Busca la respuesta de un paciente. @param idPaciente identificador del paciente. @return la respuesta encontrada. */
    Respuesta findByPaciente_IdPaciente(Long idPaciente);

    /**
     * Obtiene las respuestas de un paciente proyectadas como DTO para el psicologo.
     *
     * @param idPaciente identificador del paciente.
     * @return lista de respuestas con datos de pregunta y opcion.
     */
    @Query("""
                SELECT RespuestaPacientePsicologoResponseDTO(
                    CONCAT(pac.usuario.nombre,' ',pac.usuario.apellido),
                    pre.texto,
                    r.texto,
                    op.texto,
                    r.creadoEn
                )
                FROM Respuesta r
                JOIN r.paciente pac
                JOIN r.pregunta pre
                LEFT JOIN r.opcion op
                WHERE pac.idPaciente = :idPaciente
            """)
    List<RespuestaPacientePsicologoResponseDTO> obtenerRespuestasPaciente(@Param("idPaciente") Long idPaciente);

    /**
     * Obtiene todas las respuestas con sus relaciones cargadas eagerly
     * (pregunta, opcion, paciente y usuario) para evitar el problema N+1.
     *
     * @return lista completa de respuestas.
     */
    @Query("""
        SELECT r
        FROM Respuesta r
        JOIN FETCH r.pregunta
        LEFT JOIN FETCH r.opcion
        JOIN FETCH r.paciente p
        JOIN FETCH p.usuario
    """)
    List<Respuesta> findAllWithPaciente();
}
