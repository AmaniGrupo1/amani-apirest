package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.admin;


import com.amani.amaniapirest.dto.dtoPregunta.admin.OpcionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.OpcionAdminResDTO;
import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.repository.repositoryRespuesta.PreguntaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio PreguntaAdminService.
 *
 * Proporciona la lógica de negocio asociada a PreguntaAdminService.
 */
@Service
@RequiredArgsConstructor
public class PreguntaAdminService {

    private final PreguntaRepository preguntaRepository;


    /**
     * Método findAll.
     *
     * @return el resultado de la operación
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
     * Método create.
     *
     * @return el resultado de la operación
     */
    public PreguntaPacienteResponseDTO create(OpcionAdminResDTO request) {

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
     * Método delete.
     *
     * @return el resultado de la operación
     */
    public void delete(Long idPregunta) {

        preguntaRepository.deleteById(idPregunta);

    }

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
