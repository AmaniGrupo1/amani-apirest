package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.CitaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas (Admin)", description = "Gestion de citas — vista administrador")
public class CitaControladorAdmin {
    @Autowired
    private CitaAdminService citaService;

    /** GET /api/citas/admin — Lista todas las citas con datos completos (admin). */
    @Operation(summary = "Listar citas (admin)", description = "Lista todas las citas con datos completos")
    @GetMapping("/admin")
    public ResponseEntity<List<CitaAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(citaService.findAllAdmin());
    }

    /** GET /api/citas/admin/{id} — Obtiene una cita con datos completos (admin). */
    @Operation(summary = "Obtener cita (admin)", description = "Obtiene una cita por su ID")
    @GetMapping("/admin/{id}")
    public ResponseEntity<CitaAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findByIdAdmin(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/citas/admin — Crea una cita (admin). */
    @Operation(summary = "Crear cita (admin)", description = "Crea una nueva cita")
    @PostMapping("/admin")
    public ResponseEntity<CitaAdminResponseDTO> createAdmin(@RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(citaService.createAdmin(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/citas/admin/{id} — Actualiza una cita (admin). */
    @Operation(summary = "Actualizar cita (admin)", description = "Actualiza una cita existente")
    @PutMapping("/admin/{id}")
    public ResponseEntity<CitaAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.updateAdmin(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
