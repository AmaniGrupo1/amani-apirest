package com.amani.amaniapirest.controllers.controladorAdministador;


import com.amani.amaniapirest.dto.dtoAdmin.response.DireccionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.models.Direccion;
import com.amani.amaniapirest.services.serviceAdmin.DireccionAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de administración para la gestión CRUD de direcciones.
 *
 * <p>Base path: {@code /api/direcciones/admin}. Accesible solo por usuarios con rol admin.</p>
 */
@RestController
@RequestMapping("/api/direcciones/admin")
@Tag(name = "Direcciones (Admin)", description = "CRUD de direcciones — vista administrador")
public class DireccionAdminController {

    private final DireccionAdminService direccionService;

    public DireccionAdminController(DireccionAdminService direccionService) {
        this.direccionService = direccionService;
    }

    /**
     * Lista todas las direcciones registradas en el sistema.
     *
     * @return lista completa de direcciones
     */
    @Operation(summary = "Listar direcciones", description = "Obtiene la lista de todas las direcciones del sistema")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay direcciones registradas", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<DireccionAdminResponseDTO>> findAll() {
        return ResponseEntity.ok(direccionService.findAll());
    }

    /**
     * Obtiene una direccion por su identificador.
     *
     * @param idDireccion identificador de la direccion
     * @return la direccion encontrada
     */
    @Operation(summary = "Obtener direccion", description = "Obtiene los detalles de una direccion por su ID")
    @GetMapping("/{idDireccion}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<DireccionAdminResponseDTO> findById(@PathVariable Long idDireccion) {
        return ResponseEntity.ok(direccionService.findById(idDireccion));
    }

    /**
     * Crea una nueva direccion para un paciente.
     *
     * @param direccion datos de la direccion a crear
     * @return la direccion recien creada
     */
    @Operation(summary = "Crear direccion", description = "Crea una nueva direccion registrada en el sistema")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<DireccionAdminResponseDTO> create(@RequestBody DireccionRequestDTO direccion) {
        return ResponseEntity.ok(direccionService.create(direccion));
    }

    /**
     * Actualiza una direccion existente.
     *
     * @param idDireccion identificador de la direccion a actualizar
     * @param direccion   datos actualizados
     * @return la direccion actualizada
     */
    @Operation(summary = "Actualizar direccion", description = "Actualiza los datos de una direccion existente")
    @PutMapping("/{idDireccion}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<DireccionAdminResponseDTO> update(@PathVariable Long idDireccion,
                                                            @RequestBody Direccion direccion) {
        direccion.setIdDireccion(idDireccion);
        return ResponseEntity.ok(direccionService.update(direccion));
    }

    /**
     * Elimina una direccion por su identificador.
     *
     * @param idDireccion identificador de la direccion a eliminar
     * @return 204 No Content si se elimino correctamente, 404 si no existe
     */
    @Operation(summary = "Eliminar direccion", description = "Elimina una direccion por su ID")
    @DeleteMapping("/{idDireccion}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable Long idDireccion) {
        direccionService.delete(idDireccion);
        return ResponseEntity.noContent().build();
    }
}
