package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.services.paciente.DireccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    /** GET /api/direcciones — Lista todas las direcciones. */
    @GetMapping
    public ResponseEntity<List<DireccionResponseDTO>> findAll() {
        return ResponseEntity.ok(direccionService.findAll());
    }

    /** GET /api/direcciones/{id} — Obtiene una dirección por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(direccionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/direcciones/paciente/{idPaciente} — Lista las direcciones de un paciente. */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DireccionResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(direccionService.findByPaciente(idPaciente));
    }

    /** POST /api/direcciones — Crea una nueva dirección. */
    @PostMapping
    public ResponseEntity<DireccionResponseDTO> create(@Valid @RequestBody DireccionRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(direccionService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/direcciones/{id} — Actualiza una dirección. */
    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DireccionRequestDTO request) {
        try {
            return ResponseEntity.ok(direccionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/direcciones/{id} — Elimina una dirección. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            direccionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

