package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    @Query("""
                SELECT DISTINCT p
                FROM Pregunta p
                LEFT JOIN FETCH p.opciones
            """)
    List<Pregunta> findAllWithOpciones();
}
