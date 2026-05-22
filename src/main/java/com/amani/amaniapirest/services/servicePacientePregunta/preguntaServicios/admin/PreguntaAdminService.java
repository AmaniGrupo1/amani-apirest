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
 * Servicio que implementa la lógica de negocio para PreguntaAdmin.
 *
 * <p>Coordina las operaciones principales y gestiona las reglas de dominio.</p>
 *
 * Servicio principal que implementa la lógica de negocio de PreguntaAdminService.
 *
 * <p>Responsable de gestionar las reglas de dominio y validaciones correspondientes.</p>
 */
@Service
@RequiredArgsConstructor
public class PreguntaAdminService {

    private final PreguntaRepository preguntaRepository;


    /**
     * Obtiene y retorna la información correspondiente.
     *
     * @return Resultado de la operación o entidad procesada.
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
     * Crea y persiste un nuevo registro en el sistema.
     *
     * @return Resultado de la operación o entidad procesada.
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
     * Elimina un registro del sistema.
     *
     * @return Resultado de la operación o entidad procesada.
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
