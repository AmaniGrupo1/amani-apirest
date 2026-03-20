package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.HistorialClinicoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.HistorialClinicoAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST de administracion para la gestion CRUD de historiales clinicos.
 *
 * <p>Base path: {@code /api/admin/historial}. Accesible solo por usuarios con rol admin.</p>
 */
@RestController
@RequestMapping("/api/admin/historial")
@Tag(name = "Historial Clinico (Admin)", description = "CRUD de historiales clinicos — vista administrador")
public class HistorialClinicoAdminController {

    private final HistorialClinicoAdminService adminService;

    public HistorialClinicoAdminController(HistorialClinicoAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Lista todos los historiales clinicos del sistema.
     *
     * @return lista completa de historiales clinicos.
     */
    @Operation(summary = "Listar historiales", description = "Lista todos los historiales clinicos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<HistorialClinicoAdminResponseDTO>> findAll() {
        return ResponseEntity.ok(adminService.findAllAdmin());
    }

    /**
     * Obtiene un historial clinico por su identificador.
     *
     * @param idHistory identificador del historial.
     * @return el historial encontrado o 404 si no existe.
     */
    @Operation(summary = "Obtener historial", description = "Obtiene un historial clinico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoAdminResponseDTO> findById(@PathVariable Long idHistory) {
        try {
            return ResponseEntity.ok(adminService.findByIdAdmin(idHistory));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo historial clinico.
     *
     * @param request datos del historial a crear.
     * @return el historial recien creado.
     */
    @Operation(summary = "Crear historial", description = "Crea un nuevo historial clinico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HistorialClinicoAdminResponseDTO> create(@RequestBody HistorialClinicoRequestDTO request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    /**
     * Actualiza un historial clinico existente.
     *
     * @param idHistory identificador del historial a actualizar.
     * @param request   datos actualizados.
     * @return el historial actualizado o 404 si no existe.
     */
    @Operation(summary = "Actualizar historial", description = "Actualiza un historial clinico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{idHistory}")
    public ResponseEntity<HistorialClinicoAdminResponseDTO> update(@PathVariable Long idHistory,
                                                                   @RequestBody HistorialClinicoRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.updateAdmin(idHistory, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un historial clinico por su identificador.
     *
     * @param idHistory identificador del historial a eliminar.
     * @return 204 No Content o 404 si no existe.
     */
    @Operation(summary = "Eliminar historial", description = "Elimina un historial clinico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{idHistory}")
    public ResponseEntity<Void> delete(@PathVariable Long idHistory) {
        try {
            adminService.deleteAdmin(idHistory);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
