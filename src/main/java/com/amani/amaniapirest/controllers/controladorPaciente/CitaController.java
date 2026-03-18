package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaResponseDTO;
import com.amani.amaniapirest.services.paciente.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    // =========================================================
    // VISTA PACIENTE
    // =========================================================

    /** GET /api/citas — Lista todas las citas. */
    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> findAll() {
        return ResponseEntity.ok(citaService.findAll());
    }

    /** GET /api/citas/{id} — Obtiene una cita por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/citas — Crea una nueva cita. */
    @PostMapping
    public ResponseEntity<CitaResponseDTO> create(@Valid @RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(citaService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/citas/{id} — Actualiza una cita. */
    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/citas/{id} — Elimina una cita. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            citaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}

