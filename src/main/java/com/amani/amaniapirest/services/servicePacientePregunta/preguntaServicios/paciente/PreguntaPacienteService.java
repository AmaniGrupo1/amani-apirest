package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.paciente;


import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import com.amani.amaniapirest.repository.*;
import com.amani.amaniapirest.repository.repositoryRespuesta.OpcionRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.PreguntaRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que gestiona las preguntas del test inicial para pacientes.
 *
 * <p>Permite obtener las preguntas disponibles y registrar las respuestas
 * de un paciente asociando cada respuesta a su pregunta y opcion correspondiente.</p>
 */
@Service
@RequiredArgsConstructor
public class PreguntaPacienteService {

    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;
    private final PacientesRepository pacienteRepository;
    private final OpcionRepository opcionRepository;

    // Método privado que convierte una Pregunta en DTO
    /**
     * Convierte una entidad {@link Pregunta} a su DTO de respuesta para paciente.
     */
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
    /**
     * Obtiene todas las preguntas disponibles convertidas a DTO.
     *
     * @return lista de preguntas con sus opciones.
     */
    public List<PreguntaPacienteResponseDTO> getPreguntas(){
        return preguntaRepository.findAllWithOpciones()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Registra las respuestas de un paciente al test inicial.
     *
     * @param idPaciente identificador del paciente que responde.
     * @param respuestas lista de respuestas con pregunta, opcion y texto.
     * @throws RuntimeException si el paciente, pregunta u opcion no existen.
     */
    public void responder(Long idPaciente, List<RespuestasRequestDTO> respuestas){

        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

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
            }

            respuestaRepository.save(respuesta);
        }
    }
}
