package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.psicologo;


import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;
import com.amani.amaniapirest.repository.repositoryRespuesta.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que implementa la lógica de negocio para RespuestaPsicologo.
 *
 * <p>Coordina las operaciones principales y gestiona las reglas de dominio.</p>
 *
 * Servicio principal que implementa la lógica de negocio de RespuestaPsicologoService.
 *
 * <p>Responsable de gestionar las reglas de dominio y validaciones correspondientes.</p>
 */
@Service
@RequiredArgsConstructor
public class RespuestaPsicologoService {

    private final RespuestaRepository respuestaRepository;

    public List<RespuestaPacientePsicologoResponseDTO> getResultadosPacientes() {

        return respuestaRepository.findAllWithPaciente()
                .stream()
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                r -> r.getPaciente()
                        )
                )
                .entrySet()
                .stream()
                .map(entry -> {

                    var paciente = entry.getKey();

                    int total = entry.getValue()
                            .stream()
                            .mapToInt(r ->
                                    r.getOpcion() != null
                                            ? r.getOpcion().getValor()
                                            : 0
                            )
                            .sum();

                    String nivel;

                    if (total <= 5) {
                        nivel = "BAJO";
                    } else if (total <= 10) {
                        nivel = "MEDIO";
                    } else {
                        nivel = "ALTO";
                    }

                    return new RespuestaPacientePsicologoResponseDTO(

                    );

                })
                .toList();
    }
}
