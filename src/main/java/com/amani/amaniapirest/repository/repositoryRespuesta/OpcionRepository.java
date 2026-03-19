package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpcionRepository extends JpaRepository<Opcion, Long> {
    List<Opcion> findByPregunta_IdPregunta(Long idPregunta);
}
