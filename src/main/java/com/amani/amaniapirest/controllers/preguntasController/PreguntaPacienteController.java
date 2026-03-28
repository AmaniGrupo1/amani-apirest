package com.amani.amaniapirest.controllers.preguntasController;

import com.amani.amaniapirest.dto.dtoPregunta.ResultadoTestResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.paciente.PreguntaPacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paciente/preguntas")
@RequiredArgsConstructor
public class PreguntaPacienteController {

    private final PreguntaPacienteService preguntaPacienteService;

    @GetMapping
    public ResponseEntity<List<PreguntaPacienteResponseDTO>> getPreguntas() {
        List<PreguntaPacienteResponseDTO> pacienteResponseDTOS = preguntaPacienteService.getPreguntas();
        if (pacienteResponseDTOS.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pacienteResponseDTOS);
    }

    @PostMapping("/responder/{idPaciente}")
    public ResponseEntity<ResultadoTestResponseDTO> responder(
            @PathVariable Long idPaciente,
            @RequestBody List<RespuestasRequestDTO> respuestas
    ) {

        ResultadoTestResponseDTO resultado =
                preguntaPacienteService.responder(idPaciente, respuestas);

        return ResponseEntity.ok(resultado);
    }

}
