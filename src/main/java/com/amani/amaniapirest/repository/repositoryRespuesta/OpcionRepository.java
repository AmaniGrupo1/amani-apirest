package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Opcion}.
 */
public interface OpcionRepository extends JpaRepository<Opcion, Long> {

    /** Busca las opciones asociadas a una pregunta. @param idPregunta identificador de la pregunta. @return lista de opciones. */
    List<Opcion> findByPregunta_IdPregunta(Long idPregunta);
}
