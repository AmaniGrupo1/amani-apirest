package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.paciente;


import com.amani.amaniapirest.dto.dtoPregunta.ResultadoTestResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.OpcionRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.PreguntaRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreguntaPacienteService {

    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;
    private final PacientesRepository pacienteRepository;
    private final OpcionRepository opcionRepository;

    // Método privado que convierte una Pregunta en DTO
    private PreguntaPacienteResponseDTO mapToDTO(Pregunta pregunta){
        List<String> opciones = pregunta.getOpciones()
                .stream()
                .map(Opcion::getTexto)
                .toList();

        return new PreguntaPacienteResponseDTO(
                pregunta.getTexto(),
                pregunta.getTipo(),
                opciones
        );
    }

    // Devuelve todas las preguntas convertidas a DTO
    public List<PreguntaPacienteResponseDTO> getPreguntas(){
        return preguntaRepository.findAllWithOpciones()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ResultadoTestResponseDTO responder(
            Long idPaciente,
            List<RespuestasRequestDTO> respuestas
    ) {

        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        int total = 0;

        for (RespuestasRequestDTO r : respuestas) {

            Respuesta respuesta = new Respuesta();

            Pregunta pregunta = preguntaRepository.findById(r.getIdPregunta())
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

            respuesta.setPaciente(paciente);
            respuesta.setPregunta(pregunta);
            respuesta.setTexto(r.getTexto());
            respuesta.setCreadoEn(LocalDateTime.now());

            if (r.getIdOpcion() != null) {

                Opcion opcion = opcionRepository.findById(r.getIdOpcion())
                        .orElseThrow(() -> new RuntimeException("Opción no encontrada"));

                respuesta.setOpcion(opcion);

                if (opcion.getValor() != null) {
                    total += opcion.getValor();
                }
            }

            respuestaRepository.save(respuesta);
        }

        String nivel = calcularNivel(total);

        return new ResultadoTestResponseDTO(
                idPaciente,
                total,
                nivel
        );
    }
    private String calcularNivel(int total) {

        if (total <= 5) {
            return "BAJO";
        }

        if (total <= 10) {
            return "MEDIO";
        }

        return "ALTO";
    }
}
