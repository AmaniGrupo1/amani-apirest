package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.HistorialClinicoPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.HistorialClinicoPsicologoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psicologo/historial")
public class HistorialClinicoPsicologoController {

    private final HistorialClinicoPsicologoService psicologoService;

    public HistorialClinicoPsicologoController(HistorialClinicoPsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<HistorialClinicoPsicologoResponseDTO>> findAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(psicologoService.findAllByPsicologo(idPsicologo));
    }

    @GetMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoPsicologoResponseDTO> findById(@PathVariable Long idHistory) {
        try {
            return ResponseEntity.ok(psicologoService.findByIdPsicologo(idHistory));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoPsicologoResponseDTO> updateObservaciones(
            @PathVariable Long idHistory,
            @RequestParam(required = false) String diagnostico,
            @RequestParam(required = false) String observaciones) {
        try {
            return ResponseEntity.ok(psicologoService.updateObservaciones(idHistory, diagnostico, observaciones));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
