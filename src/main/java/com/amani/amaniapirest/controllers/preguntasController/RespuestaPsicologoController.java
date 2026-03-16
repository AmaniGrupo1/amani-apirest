package com.amani.amaniapirest.controllers.preguntasController;


import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;

import com.amani.amaniapirest.repository.repositoryRespuesta.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/psicologo/respuestas")
@RequiredArgsConstructor
public class RespuestaPsicologoController {

    private final RespuestaRepository respuestaRepository;

    @GetMapping("/{idPaciente}")
    public List<RespuestaPacientePsicologoResponseDTO> verRespuestasPaciente(
            @PathVariable Long idPaciente
    ){

        return respuestaRepository.obtenerRespuestasPaciente(idPaciente);

    }

}