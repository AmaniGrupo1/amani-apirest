package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.ProgresoEmocionalAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.ProgresoEmocionalAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para que los administradores gestionen el progreso emocional completo de los pacientes.
 */
@RestController
@RequestMapping("/admin/progreso-emocional")
public class ProgresoEmocionalAdminController {

    private final ProgresoEmocionalAdminService progresoService;

    public ProgresoEmocionalAdminController(ProgresoEmocionalAdminService progresoService) {
        this.progresoService = progresoService;
    }

    /** Listar todos los progresos */
    @GetMapping
    public ResponseEntity<List<ProgresoEmocionalAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(progresoService.findAll());
    }

    /** Obtener un progreso por ID */
    @GetMapping("/{idProgreso}")
    public ResponseEntity<ProgresoEmocionalAdminResponseDTO> getById(@PathVariable Long idProgreso) {
        return ResponseEntity.ok(progresoService.findById(idProgreso));
    }

    /** Crear un nuevo progreso emocional */
    @PostMapping
    public ResponseEntity<ProgresoEmocionalAdminResponseDTO> create(@RequestBody ProgresoEmocionalRequestDTO request) {
        return new ResponseEntity<>(progresoService.create(request), HttpStatus.CREATED);
    }

    /** Actualizar un progreso existente */
    @PutMapping("/{idProgreso}")
    public ResponseEntity<ProgresoEmocionalAdminResponseDTO> update(@PathVariable Long idProgreso,
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