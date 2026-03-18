package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.DiarioEmocionalPsicologoDTO;
import com.amani.amaniapirest.services.psicologo.DiarioEmocionPsicologoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diario/psicologo")
public class DiarioEmocionPsicologoController {

    private final DiarioEmocionPsicologoService diarioService;

    public DiarioEmocionPsicologoController(DiarioEmocionPsicologoService diarioService) {
        this.diarioService = diarioService;
    }

    @GetMapping("/{idDiario}")
    public ResponseEntity<DiarioEmocionalPsicologoDTO> getById(@PathVariable Long idDiario) {
        try {
            return ResponseEntity.ok(diarioService.findById(idDiario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
