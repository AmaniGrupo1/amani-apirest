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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que permite a un psicologo gestionar sus citas y agenda.
 *
 * <p>Base path: {@code /api/citas}. Permite listar citas, consultar agendas,
 * gestionar disponibilidad y cancelar citas.</p>
 */
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

    /**
     * Lista las citas asignadas a un psicólogo.
     *
     * @param idPsicologo identificador del psicólogo.
     * @return lista de citas del psicólogo.
     */
    @Operation(summary = "Citas por psicologo", description = "Lista todas las citas asignadas a un psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<CitaPsicologoResponseDTO>> findAllByPsicologo(@PathVariable Long idPsicologo) {
        return ResponseEntity.ok(citaService.findAllByPsicologo(idPsicologo));
    }

    /**
     * Obtiene el detalle de una cita desde la vista del psicólogo.
     *
     * @param id identificador de la cita.
     * @return los datos de la cita o 404 si no existe.
     */
    @Operation(summary = "Detalle de cita", description = "Obtiene el detalle completo de una cita")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/detalle/{id}")
    public ResponseEntity<CitaPsicologoResponseDTO> findByIdPsicologo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(citaService.findByIdPsicologo(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza el estado de una cita.
     *
     * @param id          identificador de la cita.
     * @param request     datos con el nuevo estado.
     * @return la cita actualizada o 404 si no existe.
     */
    @Operation(summary = "Actualizar estado", description = "Actualiza el estado de una cita (confirmada, cancelada, completada)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
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
     * Obtiene la agenda consolidada del psicólogo para un mes específico.
     *
     * @param idPsicologo identificador del psicólogo.
     * @param month       mes en formato YYYY-MM.
     * @return lista de elementos de la agenda.
     */
    @Operation(summary = "Agenda del psicologo", description = "Obtiene la agenda consolidada del psicólogo para un mes específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/{idPsicologo}/agenda")
    public ResponseEntity<List<AgendaItemDTO>> getAgendaPsicologoMes(
            @PathVariable Long idPsicologo,
            @RequestParam("month") String month) {
        return ResponseEntity.ok(citaAgendaService.getAgendaPsicologo(idPsicologo, month));
    }

    /**
     * Obtiene la disponibilidad del psicólogo para un día específico.
     *
     * @param idPsicologo identificador del psicólogo.
     * @param fecha       fecha en formato YYYY-MM-DD.
     * @return la disponibilidad del psicólogo para el día.
     */
    @Operation(summary = "Disponibilidad del psicologo", description = "Obtiene la disponibilidad del psicólogo para un día específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/psicologo/{idPsicologo}/disponibilidad")
    public ResponseEntity<DisponibilidadDTO> getDisponibilidadPsicologo(
            @PathVariable Long idPsicologo,
            @RequestParam("fecha") String fecha) {
        return ResponseEntity.ok(citaAgendaService.getDisponibilidad(idPsicologo, fecha));
    }

    /**
     * Cancela una cita existente.
     *
     * @param id identificador de la cita a cancelar.
     * @return la cita actualizada con estado cancelado.
     */
    @Operation(summary = "Cancelar cita", description = "Cancela una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendaItemDTO> cancelarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaAgendaService.cancelarCita(id));
    }

    /**
     * Actualiza el horario de disponibilidad del psicólogo.
     *
     * @param idPsicologo    identificador del psicólogo.
     * @param horarioRequest nuevo horario a configurar.
     * @return 204 No Content si la operación es exitosa.
     */
    @Operation(summary = "Actualizar horario", description = "Actualiza el horario de disponibilidad del psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/psicologo/{idPsicologo}/horario")
    public ResponseEntity<Void> actualizarHorarioPsicologo(
            @PathVariable Long idPsicologo,
            @RequestBody HorarioRequestDTO horarioRequest) {
        citaAgendaService.actualizarHorario(idPsicologo, horarioRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Añade un día no disponible al calendario del psicólogo.
     *
     * @param idPsicologo               identificador del psicólogo.
     * @param diaNoDisponibleRequest    datos del día bloqueado.
     * @return 204 No Content si la operación es exitosa.
     */
    @Operation(summary = "Añadir dia no disponible", description = "Añade un día no disponible al calendario del psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/psicologo/{idPsicologo}/dias-no-disponibles")
    public ResponseEntity<Void> agregarDiaNoDisponible(
            @PathVariable Long idPsicologo,
            @RequestBody BloqueoRequestDTO diaNoDisponibleRequest) {
        citaAgendaService.addBloqueo(idPsicologo, diaNoDisponibleRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un día no disponible del calendario del psicólogo.
     *
     * @param idPsicologo identificador del psicólogo.
     * @param fecha       fecha del día a desbloquear.
     * @return 204 No Content si la operación es exitosa.
     */
    @Operation(summary = "Eliminar dia no disponible", description = "Elimina un día no disponible del calendario del psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/psicologo/{idPsicologo}/dias-no-disponibles/{fecha}")
    public ResponseEntity<Void> eliminarDiaNoDisponible(@PathVariable Long idPsicologo, @PathVariable String fecha) {
        citaAgendaService.removeBloqueo(idPsicologo, fecha);
        return ResponseEntity.noContent().build();
    }
}
