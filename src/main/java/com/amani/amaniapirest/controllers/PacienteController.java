package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.dto.dtoAdmin.request.PacienteAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.services.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pacientes.
 *
 * <p>Expone endpoints diferenciados por rol:</p>
 * <ul>
 *   <li>Base ({@code /api/pacientes}) — vista paciente.</li>
 *   <li>{@code /api/pacientes/admin} — vista administrador.</li>
 *   <li>{@code /api/pacientes/psicologo} — vista psicólogo (solo lectura).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // =========================================================
    // VISTA PACIENTE
    // =========================================================

    /** GET /api/pacientes — Lista todos los pacientes. */
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

    // =========================================================
    // VISTA ADMINISTRADOR
    // =========================================================

    /** GET /api/pacientes/admin — Lista todos los pacientes con datos completos (admin). */
    @GetMapping("/admin")
    public ResponseEntity<List<PacienteAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(pacienteService.findAllAdmin());
    }

    /** GET /api/pacientes/admin/{id} — Obtiene un paciente con datos completos (admin). */
    @GetMapping("/admin/{id}")
    public ResponseEntity<PacienteAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findByIdAdmin(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/pacientes/admin — Crea un paciente (admin). */
    @PostMapping("/admin")
    public ResponseEntity<PacienteAdminResponseDTO> createAdmin(@RequestBody PacienteAdminRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.createAdmin(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/pacientes/admin/{id} — Actualiza un paciente (admin). */
    @PutMapping("/admin/{id}")
    public ResponseEntity<PacienteAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody PacienteAdminRequestDTO request) {
        try {
            return ResponseEntity.ok(pacienteService.updateAdmin(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // VISTA PSICÓLOGO
    // =========================================================

    /** GET /api/pacientes/psicologo/{id} — Obtiene los datos básicos de un paciente (psicólogo). */
    @GetMapping("/psicologo/{id}")
    public ResponseEntity<PacientePsicologoResponseDTO> findByIdPsicologo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findByIdPsicologo(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

