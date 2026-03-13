package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.AjusteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;
import com.amani.amaniapirest.services.paciente.AjusteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de ajustes de configuración de usuarios.
 *
 * <p>Base URL: {@code /api/ajustes}</p>
 */
@RestController
@RequestMapping("/api/ajustes")
public class AjusteController {

    private final AjusteService ajusteService;

    public AjusteController(AjusteService ajusteService) {
        this.ajusteService = ajusteService;
    }

    /** GET /api/ajustes — Lista todos los ajustes. */
    @GetMapping
    public ResponseEntity<List<AjusteResponseDTO>> findAll() {
        return ResponseEntity.ok(ajusteService.findAll());
    }

    /** GET /api/ajustes/{id} — Obtiene un ajuste por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<AjusteResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ajusteService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/ajustes/usuario/{idUsuario} — Obtiene los ajustes de un usuario. */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<AjusteResponseDTO> findByUsuario(@PathVariable Long idUsuario) {
        try {
            return ResponseEntity.ok(ajusteService.findByUsuario(idUsuario));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/ajustes — Crea nuevos ajustes para un usuario. */
    @PostMapping
    public ResponseEntity<AjusteResponseDTO> create(@Valid @RequestBody AjusteRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ajusteService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/ajustes/{id} — Actualiza los ajustes de un usuario. */
    @PutMapping("/{id}")
    public ResponseEntity<AjusteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AjusteRequestDTO request) {
        try {
            return ResponseEntity.ok(ajusteService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/ajustes/{id} — Elimina un ajuste. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            ajusteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

