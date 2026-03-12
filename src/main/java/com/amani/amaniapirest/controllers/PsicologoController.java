package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.dto.dtoAdmin.request.PsicologoAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PsicologoResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.request.PsicologoSelfRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.services.PsicologoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de psicólogos.
 *
 * <p>Expone endpoints diferenciados por rol:</p>
 * <ul>
 *   <li>Base ({@code /api/psicologos}) — vista paciente / general.</li>
 *   <li>{@code /api/psicologos/admin} — vista administrador.</li>
 *   <li>{@code /api/psicologos/self} — autogestión del propio psicólogo.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/psicologos")
public class PsicologoController {

    private final PsicologoService psicologoService;

    public PsicologoController(PsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    // =========================================================
    // VISTA GENERAL
    // =========================================================

    /** GET /api/psicologos — Lista todos los psicólogos. */
    @GetMapping
    public ResponseEntity<List<PsicologoResponseDTO>> findAll() {
        return ResponseEntity.ok(psicologoService.findAll());
    }

    /** GET /api/psicologos/{id} — Obtiene un psicólogo por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(psicologoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/psicologos — Crea un nuevo perfil de psicólogo. */
    @PostMapping
    public ResponseEntity<PsicologoResponseDTO> create(@Valid @RequestBody PsicologoRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(psicologoService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/psicologos/{id} — Actualiza un perfil de psicólogo. */
    @PutMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PsicologoRequestDTO request) {
        try {
            return ResponseEntity.ok(psicologoService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/psicologos/{id} — Elimina un psicólogo. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            psicologoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // VISTA ADMINISTRADOR
    // =========================================================

    /** GET /api/psicologos/admin — Lista todos los psicólogos con datos completos (admin). */
    @GetMapping("/admin")
    public ResponseEntity<List<PsicologoAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(psicologoService.findAllAdmin());
    }

    /** GET /api/psicologos/admin/{id} — Obtiene un psicólogo con datos completos (admin). */
    @GetMapping("/admin/{id}")
    public ResponseEntity<PsicologoAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(psicologoService.findByIdAdmin(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/psicologos/admin — Crea un psicólogo (admin). */
    @PostMapping("/admin")
    public ResponseEntity<PsicologoAdminResponseDTO> createAdmin(@RequestBody PsicologoAdminRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(psicologoService.createAdmin(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/psicologos/admin/{id} — Actualiza un psicólogo (admin). */
    @PutMapping("/admin/{id}")
    public ResponseEntity<PsicologoAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody PsicologoAdminRequestDTO request) {
        try {
            return ResponseEntity.ok(psicologoService.updateAdmin(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // AUTOGESTIÓN DEL PSICÓLOGO
    // =========================================================

    /** GET /api/psicologos/self/{id} — El psicólogo consulta su propio perfil. */
    @GetMapping("/self/{id}")
    public ResponseEntity<PsicologoSelfResponseDTO> findSelf(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(psicologoService.findSelf(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** PUT /api/psicologos/self/{id} — El psicólogo actualiza su propio perfil. */
    @PutMapping("/self/{id}")
    public ResponseEntity<PsicologoSelfResponseDTO> updateSelf(
            @PathVariable Long id,
            @RequestBody PsicologoSelfRequestDTO request) {
        try {
            return ResponseEntity.ok(psicologoService.updateSelf(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

