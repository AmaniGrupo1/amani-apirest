package com.amani.amaniapirest.controllers.preguntasController;


import com.amani.amaniapirest.dto.dtoPregunta.admin.OpcionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.PreguntaServicio;
import com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios.RespuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paciente/preguntas")
@RequiredArgsConstructor
public class PreguntaPacienteController {

    private final PreguntaServicio preguntaService;
    private final RespuestaService respuestaService;

    // VER PREGUNTAS
    @PreAuthorize("hasRole('paciente')")
    @GetMapping
    public ResponseEntity<List<PreguntaPacienteResponseDTO>> obtenerPreguntas() {
        var list = preguntaService.obtenerPreguntaPacientes();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('paciente')")
    @PostMapping("/responder")
    public ResponseEntity<Void> responderPregunta(
            @RequestBody RespuestasRequestDTO dto,
            @AuthenticationPrincipal Paciente paciente
    ) {

        respuestaService.responderPregunta(dto, paciente);

        return ResponseEntity.ok().build();
    }
}