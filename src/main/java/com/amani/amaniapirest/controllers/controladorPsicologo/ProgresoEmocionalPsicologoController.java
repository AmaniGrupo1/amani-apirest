package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.ProgresoEmocionalPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.ProgresoEmocionalPsicologoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psicologo/progreso-emocional")
public class ProgresoEmocionalPsicologoController {

    private final ProgresoEmocionalPsicologoService progresoService;

    public ProgresoEmocionalPsicologoController(ProgresoEmocionalPsicologoService progresoService) {
        this.progresoService = progresoService;
    }

    @GetMapping("/{idProgreso}")
    public ResponseEntity<ProgresoEmocionalPsicologoResponseDTO> getById(@PathVariable Long idProgreso) {
        return ResponseEntity.ok(progresoService.findById(idProgreso));
    }

    /** Crear un nuevo progreso emocional */
    @PostMapping
    public ResponseEntity<ProgresoEmocionalPsicologoResponseDTO> create(@RequestBody ProgresoEmocionalRequestDTO request) {
        return new ResponseEntity<>(progresoService.create(request), HttpStatus.CREATED);
    }

    /** Actualizar un progreso existente */
    @PutMapping("/{idProgreso}")
    public ResponseEntity<ProgresoEmocionalPsicologoResponseDTO> update(@PathVariable Long idProgreso,
                                                                        @RequestBody ProgresoEmocionalRequestDTO request) {
        return ResponseEntity.ok(progresoService.update(idProgreso, request));
    }

    /** Eliminar un progreso emocional */
    @DeleteMapping("/{idProgreso}")
    public ResponseEntity<Void> delete(@PathVariable Long idProgreso) {
        progresoService.delete(idProgreso);
        return ResponseEntity.noContent().build();
    }
}
