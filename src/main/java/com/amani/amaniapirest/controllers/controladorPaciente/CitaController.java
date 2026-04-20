package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaPacienteViewResponseDTO;
import com.amani.amaniapirest.services.CitaAgendaService;
import com.amani.amaniapirest.services.paciente.CitaService;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas (Paciente)", description = "Gestion de citas — vista paciente")
public class CitaController {

    private final CitaService citaService;
    private final CitaAgendaService citaAgendaService;

    public CitaController(CitaService citaService, CitaAgendaService citaAgendaService) {
        this.citaService = citaService;
        this.citaAgendaService = citaAgendaService;
    }

    // =========================================================
    // VISTA PACIENTE
    // =========================================================

    /**
     * GET /api/citas — Lista todas las citas.
     */
    @Operation(summary = "Listar citas", description = "Lista todas las citas del paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/mis-citas")
    public List<CitaPacienteViewResponseDTO> getMisCitas(Authentication auth) {
        Long idPaciente = citaService.obtenerIdPacienteDesdeAuth(auth);
        return citaService.findByPaciente(idPaciente);
    }

    //endpoint de próximas citas

    @GetMapping("/{idPaciente}/proximas")
    public List<CitaPacienteViewResponseDTO> getProximas(
            @PathVariable Long idPaciente
    ) {
        return citaService.findByPaciente(idPaciente).stream()
                .filter(c -> c.getMinutosRestantes() != null && c.getMinutosRestantes() > 0)
                .toList();
    }

    /**
     * GET /api/citas/{id} — Obtiene una cita por ID.
     */
    @Operation(summary = "Obtener cita", description = "Obtiene una cita por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CitaPacienteViewResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * PUT /api/citas/{id} — Actualiza una cita.
     */
    @Operation(summary = "Actualizar cita", description = "Actualiza una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CitaPacienteViewResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/citas/{id} — Elimina una cita.
     */
    @Operation(summary = "Eliminar cita", description = "Elimina una cita por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            citaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/citas/paciente/{idPaciente}/agenda?month=YYYY-MM
     * Devuelve la agenda del paciente para el mes indicado.
     */
    @GetMapping("/paciente/{idPaciente}/agenda")
    public ResponseEntity<List<AgendaItemDTO>> getAgendaPacienteMes(
            @PathVariable Long idPaciente,
            @RequestParam("month") String month // formato YYYY-MM
    ) {
        return ResponseEntity.ok(citaAgendaService.getAgendaPaciente(idPaciente, month));
    }

}
