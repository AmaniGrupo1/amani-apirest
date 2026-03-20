package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST de administracion para la gestion CRUD de psicologos.
 *
 * <p>Base path: {@code /api/admin/psicologos}. Permite listar, crear, actualizar
 * y eliminar perfiles de psicologo, asi como consultar las relaciones
 * paciente-psicologo.</p>
 */
@RestController
@RequestMapping("/api/admin/psicologos")
@RequiredArgsConstructor
@Tag(name = "Psicologos (Admin)", description = "CRUD de psicologos — vista administrador")
public class PsicologoAdminController {

    private final PsicologoAdminService service;

    /**
     * Lista todos los psicologos del sistema.
     *
     * @return lista de psicologos o 204 si no hay ninguno.
     */
    @Operation(summary = "Listar psicologos", description = "Lista todos los psicologos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<PsicologoAdminResponseDTO>> getAll() {
        List<PsicologoAdminResponseDTO> list = service.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Obtiene un psicologo por su identificador.
     *
     * @param id identificador del psicologo.
     * @return el psicologo encontrado o 404 si no existe.
     */
    @Operation(summary = "Obtener psicologo", description = "Obtiene un psicologo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PsicologoAdminResponseDTO> getById(@PathVariable Long id) {
        var s = service.findById(id);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(s);
    }

    /**
     * Crea un nuevo perfil de psicologo.
     *
     * @param request datos del psicologo a crear.
     * @return el psicologo creado o 400 si los datos son invalidos.
     */
    @Operation(summary = "Crear psicologo", description = "Crea un nuevo perfil de psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PsicologoAdminResponseDTO> create(@RequestBody PsicologoRequestDTO request) {
        var psicologo = service.create(request);
        if (psicologo == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(psicologo);
    }

    /**
     * Actualiza un perfil de psicologo existente.
     *
     * @param id      identificador del psicologo.
     * @param request datos actualizados.
     * @return el psicologo actualizado o 404 si no existe.
     */
    @Operation(summary = "Actualizar psicologo", description = "Actualiza un perfil de psicologo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PsicologoAdminResponseDTO> update(@PathVariable Long id,
                                                            @RequestBody PsicologoRequestDTO request) {
        var s = service.update(id, request);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(s);
    }

    /**
     * Elimina un psicologo por su identificador.
     *
     * @param id identificador del psicologo a eliminar.
     */
    @Operation(summary = "Eliminar psicologo", description = "Elimina un psicologo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Lista las relaciones entre pacientes y sus psicologos asignados.
     *
     * @return lista de pares paciente-psicologo o 204 si no hay relaciones.
     */
    @Operation(summary = "Relaciones paciente-psicologo", description = "Lista las relaciones paciente-psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/pacientes")
    public ResponseEntity<List<PsicologoAdminResponseDTO>> findAllPacientesAndPsicologos() {
        List<PsicologoAdminResponseDTO> c = service.getPacientesConPsicologo();
        if (c.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(c);
    }
}
