package com.amani.amaniapirest.controllers.controladorAdministador;


import com.amani.amaniapirest.dto.dtoAdmin.response.DiarioEmocionAdminResponseDTO;
import com.amani.amaniapirest.services.serviceAdmin.DiarioEmocionAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diario/admin")
public class DiarioEmocionAdminController {

    private final DiarioEmocionAdminService diarioService;

    public DiarioEmocionAdminController(DiarioEmocionAdminService diarioService) {
        this.diarioService = diarioService;
    }

    /** Listar todas las entradas */
    @GetMapping
    public ResponseEntity<List<DiarioEmocionAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(diarioService.findAll());
    }

    /** Obtener entrada específica */
    @GetMapping("/{idDiario}")
    public ResponseEntity<DiarioEmocionAdminResponseDTO> getById(@PathVariable Long idDiario) {
        try {
            return ResponseEntity.ok(diarioService.findById(idDiario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}