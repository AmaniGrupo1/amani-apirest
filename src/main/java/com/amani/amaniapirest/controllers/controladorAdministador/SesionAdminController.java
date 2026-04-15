package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.SesionAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de administración para la gestión CRUD de sesiones.
 *
 * <p>Base path: {@code /api/admin/sesiones}. Accesible solo por usuarios con rol admin.</p>
 */
@RestController
@RequestMapping("/api/admin/sesiones")
@Tag(name = "Sesiones (Admin)", description = "CRUD de sesiones — vista administrador")
public class SesionAdminController {

    private final SesionAdminService sesionAdminService;

    public SesionAdminController(SesionAdminService sesionAdminService) {
        this.sesionAdminService = sesionAdminService;
    }

    /**
     * Lista todas las sesiones registradas en el sistema.
     *
     * @return lista de sesiones
     */
    @Operation(summary = "Listar sesiones", description = "Obtiene la lista de todas las sesiones del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay sesiones registradas", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public List<SesionAdminResponseDTO> getAll() {
        return sesionAdminService.findAll();
    }

    /**
     * Obtiene una sesion por su identificador.
     *
     * @param idSesion identificador de la sesion
     * @return la sesion encontrada
     */
    @Operation(summary = "Obtener sesion", description = "Obtiene los detalles de una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idSesion}")
    public SesionAdminResponseDTO getById(@PathVariable Long idSesion) {
        return sesionAdminService.findById(idSesion);
    }

    /**
     * Crea una nueva sesion.
     *
     * @param request datos de la sesion a crear
     * @return la sesion recien creada
     */
    @Operation(summary = "Crear sesion", description = "Crea una nueva sesion registrada en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public SesionAdminResponseDTO create(@RequestBody SesionRequestDTO request) {
        return sesionAdminService.create(request);
    }

    /**
     * Actualiza una sesion existente.
     *
     * @param idSesion identificador de la sesion a actualizar
     * @param request  datos actualizados de la sesion
     * @return la sesion actualizada
     */
    @Operation(summary = "Actualizar sesion", description = "Actualiza los datos de una sesion existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{idSesion}")
    public SesionAdminResponseDTO update(
            @PathVariable Long idSesion,
            @RequestBody SesionRequestDTO request
    ) {
        return sesionAdminService.update(idSesion, request);
    }

    /**
     * Elimina una sesion por su identificador.
     *
     * @param idSesion identificador de la sesion a eliminar
     * @return 204 No Content si se elimino correctamente, 404 si no existe
     */
    @Operation(summary = "Eliminar sesion", description = "Elimina una sesion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{idSesion}")
    public void delete(@PathVariable Long idSesion) {
        sesionAdminService.delete(idSesion);
    }
}