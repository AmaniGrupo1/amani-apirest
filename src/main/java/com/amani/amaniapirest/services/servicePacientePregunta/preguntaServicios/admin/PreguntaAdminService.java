package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.admin;


import com.amani.amaniapirest.dto.dtoPregunta.admin.OpcionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.PreguntaRequestDTO;
import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.repository.repositoryRespuesta.OpcionRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.PreguntaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de administracion para gestionar preguntas y opciones del test inicial.
 *
 * <p>Permite listar, crear y eliminar preguntas con sus opciones asociadas.</p>
 */
@Service
@RequiredArgsConstructor
public class PreguntaAdminService {

    private final PreguntaRepository preguntaRepository;
    private final OpcionRepository opcionRepository;

    /**
     * Lista todas las preguntas con sus opciones como texto.
     *
     * @return lista de DTOs con pregunta, tipo y opciones.
     */
    public List<OpcionAdminResponseDTO> findAll() {
        return preguntaRepository.findAll()
                .stream()
                .map(pregunta -> {

                    List<String> opciones = pregunta.getOpciones()
                            .stream()
                            .map(Opcion::getTexto)
                            .toList();

                    return new OpcionAdminResponseDTO(
                            pregunta.getTexto(),
                            pregunta.getTipo(),
                            opciones,
                            pregunta.getCreadoEn()
                    );

                })
                .toList();
    }

    /**
     * Crea una nueva pregunta con sus opciones y la persiste en base de datos.
     *
     * @param request datos de la pregunta y textos de sus opciones.
     * @return DTO de la pregunta creada.
     */
    public PreguntaPacienteResponseDTO create(PreguntaRequestDTO request) {

        Pregunta pregunta = new Pregunta();
        pregunta.setTexto(request.getTexto());
        pregunta.setTipo(request.getTipo());
        pregunta.setCreadoEn(LocalDateTime.now());

        if (request.getOpciones() != null) {
            request.getOpciones().forEach(opTexto -> {
                Opcion opcion = new Opcion();
                opcion.setTexto(opTexto);
                opcion.setPregunta(pregunta); // link bidireccional
                pregunta.getOpciones().add(opcion);
            });
        }

        preguntaRepository.save(pregunta);

        return miPregunta(pregunta);
    }

    /**
     * Elimina una pregunta por su identificador (en cascada se eliminan sus opciones).
     *
     * @param idPregunta identificador de la pregunta a eliminar.
     */
    public void delete(Long idPregunta) {

        preguntaRepository.deleteById(idPregunta);

    }

    /**
     * Convierte una entidad {@link Pregunta} a su DTO de respuesta para paciente.
     */
    private PreguntaPacienteResponseDTO miPregunta(Pregunta pregunta) {
        List<String> opciones = pregunta.getOpciones()
                .stream()
                .map(Opcion::getTexto)
                .toList();
        PreguntaPacienteResponseDTO p = new PreguntaPacienteResponseDTO(
                pregunta.getTexto(),
                pregunta.getTipo(),
                opciones
        );
        return p;
    }
}
