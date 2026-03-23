package com.amani.amaniapirest.controllers.controladorPsicologo;

import com.amani.amaniapirest.dto.dtoAgenda.request.BloqueoRequestDTO;
import com.amani.amaniapirest.dto.dtoAgenda.request.HorarioRequestDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.DisponibilidadDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.services.CitaAgendaService;
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
    private final CitaAgendaService citaAgendaService;

    public CitaControladorPsicologo(CitaServicePsicologo citaService, CitaAgendaService citaAgendaService) {
        this.citaService = citaService;
        this.citaAgendaService = citaAgendaService;
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

    /**
     * GET /api/citas/psicologo/{idPsicologo}/agenda?month=YYYY-MM
     * Devuelve la agenda consolidada del psicólogo para el mes indicado.
     */
    @GetMapping("/psicologo/{idPsicologo}/agenda")
    public ResponseEntity<List<AgendaItemDTO>> getAgendaPsicologoMes(
            @PathVariable Long idPsicologo,
            @RequestParam("month") String month // formato YYYY-MM
    ) {
        return ResponseEntity.ok(citaAgendaService.getAgendaPsicologo(idPsicologo, month));
    }

    /**
     * GET /api/citas/psicologo/{idPsicologo}/disponibilidad?fecha=YYYY-MM-DD
     * Devuelve la disponibilidad del psicólogo para un día concreto.
     */
    @GetMapping("/psicologo/{idPsicologo}/disponibilidad")
    public ResponseEntity<DisponibilidadDTO> getDisponibilidadPsicologo(
            @PathVariable Long idPsicologo,
            @RequestParam("fecha") String fecha
    ) {
        return ResponseEntity.ok(citaAgendaService.getDisponibilidad(idPsicologo, fecha));
    }

    /**
     * PATCH /api/citas/{id}/cancelar — Cancela una cita.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendaItemDTO> cancelarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaAgendaService.cancelarCita(id));
    }

    /**
     * PUT /api/citas/psicologo/{idPsicologo}/horario — Actualiza el horario del psicólogo.
     */
    @PutMapping("/psicologo/{idPsicologo}/horario")
    public ResponseEntity<Void> actualizarHorarioPsicologo(
            @PathVariable Long idPsicologo,
            @RequestBody HorarioRequestDTO horarioRequest
    ) {
        citaAgendaService.actualizarHorario(idPsicologo, horarioRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/citas/psicologo/{idPsicologo}/dias-no-disponibles — Añade un día no disponible.
     */
    @PostMapping("/psicologo/{idPsicologo}/dias-no-disponibles")
    public ResponseEntity<Void> agregarDiaNoDisponible(
            @PathVariable Long idPsicologo,
            @RequestBody BloqueoRequestDTO diaNoDisponibleRequest
    ) {
        citaAgendaService.addBloqueo(idPsicologo, diaNoDisponibleRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/citas/psicologo/{idPsicologo}/dias-no-disponibles/{fecha} — Elimina un día no disponible.
     */
    @DeleteMapping("/psicologo/{idPsicologo}/dias-no-disponibles/{fecha}")
    public ResponseEntity<Void> eliminarDiaNoDisponible(@PathVariable Long idPsicologo, @PathVariable String fecha) {
        citaAgendaService.removeBloqueo(idPsicologo, fecha);
        return ResponseEntity.noContent().build();
    }
}
