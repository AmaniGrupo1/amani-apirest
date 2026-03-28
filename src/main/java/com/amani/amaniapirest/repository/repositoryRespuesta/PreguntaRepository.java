package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Pregunta}.
 */
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    /**
     * Obtiene todas las preguntas con sus opciones cargadas eagerly (FETCH JOIN)
     * para evitar el problema N+1.
     *
     * @return lista de preguntas con opciones.
     */
    @Query("""
                SELECT DISTINCT p
                FROM Pregunta p
                LEFT JOIN FETCH p.opciones
            """)
    List<Pregunta> findAllWithOpciones();
}
