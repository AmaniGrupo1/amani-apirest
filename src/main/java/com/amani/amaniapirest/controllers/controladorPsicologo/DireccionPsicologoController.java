package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.DireccionPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.DireccionPsicologoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones/psicologo")
public class DireccionPsicologoController {

    private final DireccionPsicologoService direccionService;

    public DireccionPsicologoController(DireccionPsicologoService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping("/{idPaciente}")
    public ResponseEntity<List<DireccionPsicologoResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(direccionService.findByPaciente(idPaciente));
    }

    @GetMapping("/detalle/{idPaciente}")
    public ResponseEntity<List<DireccionPsicologoResponseDTO>> findDetalle(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(direccionService.findByPaciente(idPaciente));
    }

}
