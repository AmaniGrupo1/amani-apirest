package com.amani.amaniapirest.controllers.preguntasController;



import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.RespuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paciente/respuestas")
@RequiredArgsConstructor
public class RespuestaPacienteController {

    private final RespuestaService respuestaService;

    @PostMapping
    public void responder(
            @RequestBody RespuestasRequestDTO dto,
            Paciente paciente
    ){
        respuestaService.responderPregunta(dto,paciente);
    }
}
