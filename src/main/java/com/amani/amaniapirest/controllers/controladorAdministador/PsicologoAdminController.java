package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.request.AsignarPacienteAlPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.PsicologoConPacientesDTO;
import com.amani.amaniapirest.services.psicologo.PsicologoSelfService;
import com.amani.amaniapirest.services.serviceAdmin.PacienteAdminService;
import com.amani.amaniapirest.services.serviceAdmin.PsicologoAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de administración para la gestión de psicólogos.
 *
 * <p>Base path: {@code /api/admin/psicologos}. Permite asignar pacientes, crear,
 * listar, actualizar y eliminar psicólogos.</p>
 */
@RestController
@RequestMapping("/api/admin/psicologos")
@RequiredArgsConstructor
@Tag(name = "Psicólogos (Admin)", description = "Gestión de psicólogos — vista administrador")
public class PsicologoAdminController {

    private final PsicologoAdminService service;
    private final PsicologoSelfService selfService;
    private final PacienteAdminService psicologoPacienteService;

    /**
     * Asigna un paciente a un psicólogo.
     *
     * @param request datos de la asignación (ID de paciente y psicólogo)
     * @return {@code true} si la asignación fue exitosa, {@code false} en caso contrario
     */
    @Operation(summary = "Asignar paciente a psicologo", description = "Crea una relación entre un paciente y un psicologo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o asignación inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/asignar-psicologo")
    public ResponseEntity<Boolean> asignarPsicologo(@RequestBody AsignarPacienteAlPsicologoRequestDTO request) {
        boolean resultado = psicologoPacienteService.asignarPsicologo(
                request.getIdPaciente(),
                request.getIdPsicologo()
        );
        if (resultado) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    /**
     * Lista todos los psicólogos registrados en el sistema.
     *
     * @return lista de psicólogos
     */
    @Operation(summary = "Listar psicologos", description = "Obtiene la lista de todos los psicologos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay psicólogos registrados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<PsicologoSelfResponseDTO>> findAllPsicologo() {
        List<PsicologoSelfResponseDTO> user = selfService.findAll();
        if (user.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(user);
    }

    /**
     * Crea un nuevo psicólogo en el sistema.
     *
     * @param request datos del psicólogo a crear
     * @return el psicólogo creado
     */
    @Operation(summary = "Crear psicologo", description = "Crea un nuevo registro de psicólogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<PsicologoSelfResponseDTO> create(@RequestBody PsicologoRequestDTO request) {
        var psi = selfService.create(request);
        if (psi == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(psi);
    }

    /**
     * Actualiza los datos de un psicólogo existente.
     *
     * @param id      identificador del psicólogo a actualizar
     * @param request datos actualizados del psicólogo
     * @return el psicólogo actualizado o 404 si no existe
     */
    @Operation(summary = "Actualizar psicologo", description = "Actualiza un registro de psicólogo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PsicologoConPacientesDTO> update(@PathVariable Long id, @RequestBody PsicologoRequestDTO request) {
        try {
            return ResponseEntity.ok(service.update(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un psicólogo por su identificador.
     *
     * @param id identificador del psicólogo a eliminar
     * @return 204 No Content si se eliminó correctamente, 404 si no existe
     */
    @Operation(summary = "Eliminar psicologo", description = "Elimina un registro de psicólogo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos los psicólogos con sus pacientes asignados.
     *
     * @return lista de psicólogos con pacientes
     */
    @Operation(summary = "Listar psicologos con pacientes", description = "Obtiene la lista de psicologos junto con sus pacientes asignados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay psicólogos con pacientes asignados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/pacientes")
    public ResponseEntity<List<PsicologoConPacientesDTO>> findAllPacientesAndPsicologos() {
        List<PsicologoConPacientesDTO> list = service.getPsicologosConPacientes();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }
}