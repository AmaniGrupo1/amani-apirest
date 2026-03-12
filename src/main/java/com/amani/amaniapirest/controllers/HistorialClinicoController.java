package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.services.HistorialClinicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del historial clínico de pacientes.
 *
 * <p>Base URL: {@code /api/historial-clinico}</p>
 */
@RestController
@RequestMapping("/api/historial-clinico")
public class HistorialClinicoController {

    private final HistorialClinicoService historialClinicoService;

    public HistorialClinicoController(HistorialClinicoService historialClinicoService) {
        this.historialClinicoService = historialClinicoService;
    }

    /** GET /api/historial-clinico — Lista todos los registros clínicos. */
    @GetMapping
    public ResponseEntity<List<HistorialClinicoResponseDTO>> findAll() {
        return ResponseEntity.ok(historialClinicoService.findAll());
    }

    /** GET /api/historial-clinico/{id} — Obtiene un registro clínico por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<HistorialClinicoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(historialClinicoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/historial-clinico/paciente/{idPaciente} — Lista el historial de un paciente. */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<HistorialClinicoResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(historialClinicoService.findByPaciente(idPaciente));
    }

    /** POST /api/historial-clinico — Crea un nuevo registro clínico. */
    @PostMapping
    public ResponseEntity<HistorialClinicoResponseDTO> create(@Valid @RequestBody HistorialClinicoRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(historialClinicoService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/historial-clinico/{id} — Actualiza un registro clínico. */
    @PutMapping("/{id}")
    public ResponseEntity<HistorialClinicoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody HistorialClinicoRequestDTO request) {
        try {
            return ResponseEntity.ok(historialClinicoService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/historial-clinico/{id} — Elimina un registro clínico. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            historialClinicoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

