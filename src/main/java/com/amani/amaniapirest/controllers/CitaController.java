package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.dto.dtoAdmin.request.CitaAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.request.CitaPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.services.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de citas.
 *
 * <p>Expone endpoints diferenciados por rol:</p>
 * <ul>
 *   <li>Base ({@code /api/citas}) — vista paciente.</li>
 *   <li>{@code /api/citas/admin} — vista administrador.</li>
 *   <li>{@code /api/citas/psicologo} — vista psicólogo.</li>
 * </ul>
 */
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

    // =========================================================
    // VISTA ADMINISTRADOR
    // =========================================================

    /** GET /api/citas/admin — Lista todas las citas con datos completos (admin). */
    @GetMapping("/admin")
    public ResponseEntity<List<CitaAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(citaService.findAllAdmin());
    }

    /** GET /api/citas/admin/{id} — Obtiene una cita con datos completos (admin). */
    @GetMapping("/admin/{id}")
    public ResponseEntity<CitaAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findByIdAdmin(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/citas/admin — Crea una cita (admin). */
    @PostMapping("/admin")
    public ResponseEntity<CitaAdminResponseDTO> createAdmin(@RequestBody CitaAdminRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(citaService.createAdmin(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/citas/admin/{id} — Actualiza una cita (admin). */
    @PutMapping("/admin/{id}")
    public ResponseEntity<CitaAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody CitaAdminRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.updateAdmin(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // VISTA PSICÓLOGO
    // =========================================================

    /** GET /api/citas/psicologo/{idPsicologo} — Lista las citas asignadas a un psicólogo. */
    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<CitaPsicologoResponseDTO>> findAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(citaService.findAllByPsicologo(idPsicologo));
    }

    /** GET /api/citas/psicologo/detalle/{id} — Obtiene una cita desde la vista del psicólogo. */
    @GetMapping("/psicologo/detalle/{id}")
    public ResponseEntity<CitaPsicologoResponseDTO> findByIdPsicologo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findByIdPsicologo(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** PATCH /api/citas/psicologo/{id}/estado — Actualiza el estado de una cita (psicólogo). */
    @PatchMapping("/psicologo/{id}/estado")
    public ResponseEntity<CitaPsicologoResponseDTO> updateEstado(
            @PathVariable Long id,
            @RequestBody CitaPsicologoRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.updateEstadoCita(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

