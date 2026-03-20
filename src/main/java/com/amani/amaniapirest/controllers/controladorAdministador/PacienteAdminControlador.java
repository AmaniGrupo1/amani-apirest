package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.PacienteAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes (Admin)", description = "Gestion de pacientes — vista administrador")
public class PacienteAdminControlador {

    private final PacienteAdminService pacienteService;



    /** GET /api/pacientes/admin — Lista todos los pacientes con datos completos (admin). */
    @Operation(summary = "Listar pacientes (admin)", description = "Lista todos los pacientes con datos completos")
    @GetMapping("/admin")
    public ResponseEntity<List<PacienteAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    /** GET /api/pacientes/admin/{id} — Obtiene un paciente con datos completos (admin). */
    @Operation(summary = "Obtener paciente (admin)", description = "Obtiene un paciente por su ID")
    @GetMapping("/admin/{id}")
    public ResponseEntity<PacienteAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/pacientes/admin — Crea un paciente (admin). */
    @Operation(summary = "Crear paciente (admin)", description = "Crea un nuevo paciente")
    @PostMapping("/admin")
    public ResponseEntity<PacienteAdminResponseDTO> createAdmin(@RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PUT /api/pacientes/admin/{id} — Actualiza un paciente (admin). */
    @Operation(summary = "Actualizar paciente (admin)", description = "Actualiza un paciente existente")
    @PutMapping("/admin/{id}")
    public ResponseEntity<PacienteAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.ok(pacienteService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }


}
