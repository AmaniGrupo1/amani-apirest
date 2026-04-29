package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.TicketSoporteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.TicketSoporteResponseDTO;
import com.amani.amaniapirest.enums.EstadoTicketSoporte;
import com.amani.amaniapirest.services.paciente.SoporteTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestion de tickets de soporte del usuario autenticado.
 *
 * <p>Base path: {@code /api/tickets-soporte}.</p>
 */
@RestController
@RequestMapping("/api/tickets-soporte")
@Tag(name = "Tickets de Soporte", description = "Gestion de tickets de soporte del usuario autenticado")
public class SoporteTicketController {

    private final SoporteTicketService soporteTicketService;

    public SoporteTicketController(SoporteTicketService soporteTicketService) {
        this.soporteTicketService = soporteTicketService;
    }

    /**
     * Lista todos los tickets del usuario autenticado.
     *
     * @param estado filtro opcional por estado (abierto, en_progreso, cerrado)
     * @return lista de tickets ordenados del mas reciente al mas antiguo
     */
    @Operation(summary = "Listar mis tickets", description = "Recupera todos los tickets de soporte del usuario autenticado, opcionalmente filtrados por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TicketSoporteResponseDTO>> findMisTickets(
            @RequestParam(required = false) EstadoTicketSoporte estado) {
        return ResponseEntity.ok(soporteTicketService.findMisTickets(estado));
    }

    /**
     * Obtiene un ticket especifico del usuario autenticado por su ID.
     *
     * @param id identificador unico del ticket
     * @return datos del ticket
     */
    @Operation(summary = "Obtener ticket", description = "Recupera un ticket de soporte especifico del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para ver este ticket", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontro ningun ticket con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TicketSoporteResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(soporteTicketService.findById(id));
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("permiso")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo ticket de soporte para el usuario autenticado.
     *
     * @param request datos del ticket a crear
     * @return ticket creado
     */
    @Operation(summary = "Crear ticket", description = "Crea un nuevo ticket de soporte para el usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TicketSoporteResponseDTO> create(@Valid @RequestBody TicketSoporteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soporteTicketService.create(request));
    }
}
