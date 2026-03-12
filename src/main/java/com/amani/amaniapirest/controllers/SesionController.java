package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.dto.dtoAdmin.request.SesionAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.SesionResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.request.SesionPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO;
import com.amani.amaniapirest.services.SesionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de sesiones terapéuticas.
 *
 * <p>Expone endpoints diferenciados por rol:</p>
 * <ul>
 *   <li>Base ({@code /api/sesiones}) — vista paciente / general.</li>
 *   <li>{@code /api/sesiones/admin} — vista administrador.</li>
 *   <li>{@code /api/sesiones/psicologo} — vista psicólogo.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

    private final SesionService sesionService;

    public SesionController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    // =========================================================
    // VISTA GENERAL / PACIENTE
    // =========================================================

    /** GET /api/sesiones — Lista todas las sesiones. */
    @GetMapping
    public ResponseEntity<List<SesionResponseDTO>> findAll() {
        return ResponseEntity.ok(sesionService.findAll());
    }

    /** GET /api/sesiones/{id} — Obtiene una sesión por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<SesionResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sesionService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/sesiones/paciente/{idPaciente} — Lista las sesiones de un paciente. */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<SesionResponseDTO>> findAllByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(sesionService.findAllByPaciente(idPaciente));
    }

    /** POST /api/sesiones — Crea una nueva sesión. */
    @PostMapping
    public ResponseEntity<SesionResponseDTO> create(@Valid @RequestBody SesionRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sesionService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/sesiones/{id} — Actualiza una sesión. */
    @PutMapping("/{id}")
    public ResponseEntity<SesionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SesionRequestDTO request) {
        try {
            return ResponseEntity.ok(sesionService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/sesiones/{id} — Elimina una sesión. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            sesionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // VISTA ADMINISTRADOR
    // =========================================================

    /** GET /api/sesiones/admin — Lista todas las sesiones con datos completos (admin). */
    @GetMapping("/admin")
    public ResponseEntity<List<SesionAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(sesionService.findAllAdmin());
    }

    /** GET /api/sesiones/admin/{id} — Obtiene una sesión con datos completos (admin). */
    @GetMapping("/admin/{id}")
    public ResponseEntity<SesionAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sesionService.findByIdAdmin(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/sesiones/admin — Crea una sesión (admin). */
    @PostMapping("/admin")
    public ResponseEntity<SesionAdminResponseDTO> createAdmin(@RequestBody SesionAdminRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sesionService.createAdmin(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/sesiones/admin/{id} — Actualiza una sesión (admin). */
    @PutMapping("/admin/{id}")
    public ResponseEntity<SesionAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody SesionAdminRequestDTO request) {
        try {
            return ResponseEntity.ok(sesionService.updateAdmin(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // VISTA PSICÓLOGO
    // =========================================================

    /** GET /api/sesiones/psicologo/{idPsicologo} — Lista las sesiones de un psicólogo. */
    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<SesionPsicologoResponseDTO>> findAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(sesionService.findAllByPsicologo(idPsicologo));
    }

    /** POST /api/sesiones/psicologo — El psicólogo registra una nueva sesión. */
    @PostMapping("/psicologo")
    public ResponseEntity<SesionPsicologoResponseDTO> createPsicologo(@RequestBody SesionPsicologoRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sesionService.createPsicologo(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}

