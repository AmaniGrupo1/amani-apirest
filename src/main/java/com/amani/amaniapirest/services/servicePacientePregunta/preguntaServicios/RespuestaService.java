package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios;


import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import com.amani.amaniapirest.repository.repositoryRespuesta.OpcionRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.PreguntaRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de negocio para registrar y consultar las respuestas al cuestionario inicial.
 */
@Service
@RequiredArgsConstructor
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionRepository opcionRepository;

    public void responderPregunta(RespuestasRequestDTO dto, Paciente paciente){

        Pregunta pregunta = preguntaRepository.findById(dto.getIdPregunta())
                .orElseThrow();

        Respuesta respuesta = new Respuesta();

        respuesta.setPaciente(paciente);
        respuesta.setPregunta(pregunta);
        respuesta.setTexto(dto.getTexto());

        if(dto.getIdOpcion() != null){

            respuesta.setOpcion(
                    opcionRepository.findById(dto.getIdOpcion()).orElseThrow()
            );

        }

        respuestaRepository.save(respuesta);

    }

}