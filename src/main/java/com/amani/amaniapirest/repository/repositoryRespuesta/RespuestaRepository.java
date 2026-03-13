package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    Respuesta findByPaciente_IdPaciente(Long idPaciente);
}
