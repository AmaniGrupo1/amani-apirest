package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.psicologo;


import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;
import com.amani.amaniapirest.repository.repositoryRespuesta.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que permite a un psicologo consultar las respuestas de los pacientes
 * al test inicial.
 */
@Service
@RequiredArgsConstructor
public class RespuestaPsicologoService {

    private final RespuestaRepository respuestaRepository;

    /**
     * Obtiene todas las respuestas de todos los pacientes, mapeadas a DTO
     * con datos del paciente, pregunta, respuesta libre y opcion seleccionada.
     *
     * @return lista de respuestas proyectadas como DTOs para el psicologo.
     */
    public List<RespuestaPacientePsicologoResponseDTO> getRespuestasPacientes(){

        return respuestaRepository.findAllWithPaciente()
                .stream()
                .map(r -> new RespuestaPacientePsicologoResponseDTO(
                        r.getPaciente().getUsuario().getNombre(),
                        r.getPregunta().getTexto(),
                        r.getTexto(),
                        r.getOpcion()!=null ? r.getOpcion().getTexto() : null,
                        r.getCreadoEn()
                ))
                .toList();
    }
}
