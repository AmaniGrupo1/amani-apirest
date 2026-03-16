package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.services.paciente.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> findAll() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    /** GET /api/pacientes/{id} — Obtiene un paciente por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/pacientes — Crea un nuevo perfil de paciente. */
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> create(@Valid @RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/pacientes/{id} — Actualiza un perfil de paciente. */
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.ok(pacienteService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/pacientes/{id} — Elimina un paciente. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            pacienteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}

