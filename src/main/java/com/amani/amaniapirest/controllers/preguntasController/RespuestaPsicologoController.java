package com.amani.amaniapirest.controllers.preguntasController;


import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.psicologo.RespuestaPsicologoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psicologo/respuestas")
@RequiredArgsConstructor
public class RespuestaPsicologoController {

    private final RespuestaPsicologoService respuestaPsicologoService;

    @GetMapping
    public List<RespuestaPacientePsicologoResponseDTO> getRespuestasPacientes() {
        return respuestaPsicologoService.getResultadosPacientes();
    }
}
