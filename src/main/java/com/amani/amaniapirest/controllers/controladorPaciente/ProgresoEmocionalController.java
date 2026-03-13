package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.ProgresoEmocionalResponseDTO;
import com.amani.amaniapirest.services.paciente.ProgresoEmocionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del progreso emocional de pacientes.
 *
 * <p>Base URL: {@code /api/progreso-emocional}</p>
 */
@RestController
@RequestMapping("/api/progreso-emocional")
public class ProgresoEmocionalController {

    private final ProgresoEmocionalService progresoEmocionalService;

    public ProgresoEmocionalController(ProgresoEmocionalService progresoEmocionalService) {
        this.progresoEmocionalService = progresoEmocionalService;
    }

    /** GET /api/progreso-emocional — Lista todos los registros de progreso. */
    @GetMapping
    public ResponseEntity<List<ProgresoEmocionalResponseDTO>> findAll() {
        return ResponseEntity.ok(progresoEmocionalService.findAll());
    }

    /** GET /api/progreso-emocional/{id} — Obtiene un registro de progreso por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ProgresoEmocionalResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(progresoEmocionalService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/progreso-emocional/paciente/{idPaciente} — Lista el progreso de un paciente. */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ProgresoEmocionalResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(progresoEmocionalService.findByPaciente(idPaciente));
    }

    /** POST /api/progreso-emocional — Crea un nuevo registro de progreso. */
    @PostMapping
    public ResponseEntity<ProgresoEmocionalResponseDTO> create(@Valid @RequestBody ProgresoEmocionalRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(progresoEmocionalService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/progreso-emocional/{id} — Actualiza un registro de progreso. */
    @PutMapping("/{id}")
    public ResponseEntity<ProgresoEmocionalResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgresoEmocionalRequestDTO request) {
        try {
            return ResponseEntity.ok(progresoEmocionalService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/progreso-emocional/{id} — Elimina un registro de progreso. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            progresoEmocionalService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

