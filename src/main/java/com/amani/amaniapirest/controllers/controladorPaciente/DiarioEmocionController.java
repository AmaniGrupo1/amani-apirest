package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.DiarioEmocionRequestDTO;

import com.amani.amaniapirest.dto.dtoPaciente.response.DiarioEmocionResponseDTO;
import com.amani.amaniapirest.services.paciente.DiarioEmocionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del diario emocional de pacientes.
 *
 * <p>Base URL: {@code /api/diario-emocion}</p>
 */
@RestController
@RequestMapping("/api/diario-emocion")
public class DiarioEmocionController {

    private final DiarioEmocionService diarioEmocionService;

    public DiarioEmocionController(DiarioEmocionService diarioEmocionService) {
        this.diarioEmocionService = diarioEmocionService;
    }

    /** GET /api/diario-emocion — Lista todas las entradas del diario. */
    @GetMapping
    public ResponseEntity<List<DiarioEmocionResponseDTO>> findAll() {
        return ResponseEntity.ok(diarioEmocionService.findAll());
    }

    /** GET /api/diario-emocion/{id} — Obtiene una entrada por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<DiarioEmocionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(diarioEmocionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/diario-emocion/paciente/{idPaciente} — Lista las entradas de un paciente. */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DiarioEmocionResponseDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(diarioEmocionService.findByPaciente(idPaciente));
    }

    /** POST /api/diario-emocion — Crea una nueva entrada en el diario. */
    @PostMapping
    public ResponseEntity<DiarioEmocionResponseDTO> create(@Valid @RequestBody DiarioEmocionRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(diarioEmocionService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/diario-emocion/{id} — Actualiza una entrada del diario. */
    @PutMapping("/{id}")
    public ResponseEntity<DiarioEmocionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DiarioEmocionRequestDTO request) {
        try {
            return ResponseEntity.ok(diarioEmocionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/diario-emocion/{id} — Elimina una entrada del diario. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            diarioEmocionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

