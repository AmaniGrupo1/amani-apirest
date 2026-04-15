package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.CitaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de administración para la gestión de citas.
 *
 * <p>Base path: {@code /api/citas}. Permite listar, obtener, crear y actualizar citas.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/citas")
@Tag(name = "Citas (Admin)", description = "Gestión de citas — vista administrador")
public class CitaControladorAdmin {
    private final CitaAdminService citaService;

    /**
     * Lista todas las citas con datos completos (admin).
     *
     * @return lista de citas
     */
    @Operation(summary = "Listar citas (admin)", description = "Obtiene la lista de todas las citas con datos completos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay citas registradas", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/admin")
    public ResponseEntity<List<CitaAdminResponseDTO>> findAllAdmin() {
        List<CitaAdminResponseDTO> c = citaService.findAllAdmin();
        if (c.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(c);
    }

    /**
     * Obtiene una cita con datos completos por su identificador (admin).
     *
     * @param id identificador de la cita
     * @return la cita encontrada
     */
    @Operation(summary = "Obtener cita (admin)", description = "Obtiene los detalles de una cita por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/admin/{id}")
    public ResponseEntity<CitaAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.findByIdAdmin(id));
    }

    /**
     * Crea una nueva cita (admin).
     *
     * @param request datos de la cita a crear
     * @return la cita recien creada
     */
    @Operation(summary = "Crear cita (admin)", description = "Crea una nueva cita registrada en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/admin")
    public ResponseEntity<CitaAdminResponseDTO> createCitaAdmin(@RequestBody CitaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.createAdmin(request));
    }

    /**
     * Actualiza una cita existente (admin).
     *
     * @param id      identificador de la cita a actualizar
     * @param request datos actualizados de la cita
     * @return la cita actualizada
     */
    @Operation(summary = "Actualizar cita (admin)", description = "Actualiza los datos de una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/admin/{id}")
    public ResponseEntity<CitaAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody CitaRequestDTO request) {

        return ResponseEntity.ok(citaService.updateAdmin(id, request));
    }


}
