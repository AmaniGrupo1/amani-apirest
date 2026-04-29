package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.request.TicketSoporteEstadoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.TicketSoporteResponseDTO;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.services.serviceAdmin.SoporteTicketAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de administracion para la gestion de tickets de soporte.
 *
 * <p>Base path: {@code /api/tickets-soporte/admin}. Solo usuarios con rol ADMIN.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets-soporte/admin")
@Tag(name = "Tickets de Soporte (Admin)", description = "Gestion de tickets de soporte — vista administrador")
public class SoporteTicketAdminController {

    private final SoporteTicketAdminService soporteTicketAdminService;

    /**
     * Lista todos los tickets de soporte del sistema (admin).
     *
     * @param estado filtro opcional por estado
     * @return lista de tickets
     */
    @Operation(summary = "Listar tickets (admin)", description = "Obtiene la lista de todos los tickets de soporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay tickets registrados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TicketSoporteResponseDTO>> findAll(
            @RequestParam(required = false) EstadoTicketSoporte estado) {
        if (estado != null) {
            List<TicketSoporteResponseDTO> list = soporteTicketAdminService.findByEstado(estado);
            if (list.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(list);
        }
        List<TicketSoporteResponseDTO> list = soporteTicketAdminService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene un ticket de soporte por su identificador (admin).
     *
     * @param id identificador del ticket
     * @return ticket encontrado
     */
    @Operation(summary = "Obtener ticket (admin)", description = "Obtiene los detalles de un ticket por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TicketSoporteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(soporteTicketAdminService.findById(id));
    }

    /**
     * Actualiza el estado de un ticket de soporte (admin).
     *
     * @param id      identificador del ticket
     * @param request DTO con el nuevo estado
     * @return ticket actualizado
     */
    @Operation(summary = "Actualizar estado ticket (admin)", description = "Actualiza el estado de un ticket de soporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<TicketSoporteResponseDTO> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody TicketSoporteEstadoRequestDTO request) {
        return ResponseEntity.ok(soporteTicketAdminService.updateEstado(id, request));
    }

    /**
     * Elimina un ticket de soporte por su identificador (admin).
     *
     * @param id identificador del ticket a eliminar
     */
    @Operation(summary = "Eliminar ticket (admin)", description = "Elimina un ticket de soporte por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        soporteTicketAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
