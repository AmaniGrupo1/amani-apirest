package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
}
