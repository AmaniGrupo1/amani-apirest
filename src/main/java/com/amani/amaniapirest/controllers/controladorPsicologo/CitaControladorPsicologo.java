package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.services.psicologo.CitaServicePsicologo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas (Psicologo)", description = "Gestion de citas — vista psicologo")
public class CitaControladorPsicologo {

    private final CitaServicePsicologo citaService;

    public CitaControladorPsicologo(CitaServicePsicologo citaService) {
        this.citaService = citaService;
    }
    // =========================================================
    // VISTA PSICÓLOGO
    // =========================================================

    /** GET /api/citas/psicologo/{idPsicologo} — Lista las citas asignadas a un psicólogo. */
    @Operation(summary = "Citas por psicologo", description = "Lista las citas asignadas a un psicologo")
    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<CitaPsicologoResponseDTO>> findAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(citaService.findAllByPsicologo(idPsicologo));
    }

    /** GET /api/citas/psicologo/detalle/{id} — Obtiene una cita desde la vista del psicólogo. */
    @Operation(summary = "Detalle de cita", description = "Obtiene una cita desde la vista del psicologo")
    @GetMapping("/psicologo/detalle/{id}")
    public ResponseEntity<CitaPsicologoResponseDTO> findByIdPsicologo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findByIdPsicologo(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** PATCH /api/citas/psicologo/{id}/estado — Actualiza el estado de una cita (psicólogo). */
    @Operation(summary = "Actualizar estado", description = "Actualiza el estado de una cita")
    @PatchMapping("/psicologo/{id}/estado")
    public ResponseEntity<CitaPsicologoResponseDTO> updateEstado(
            @PathVariable Long id,
            @RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.updateEstadoCita(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
