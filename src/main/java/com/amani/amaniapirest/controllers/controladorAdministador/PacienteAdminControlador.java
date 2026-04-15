package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.services.serviceAdmin.PacienteAdminService;
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
 * Controlador REST para la gestión de pacientes desde la vista administrador.
 *
 * <p>Expone endpoints para consulta, creación y actualización de perfiles
 * de pacientes con fines administrativos.</p>
 */
@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes (Admin)", description = "Gestion de pacientes — vista administrador")
public class PacienteAdminControlador {

    private final PacienteAdminService pacienteService;



    /**
     * Lista todos los pacientes con datos completos (vista admin).
     *
     * @return lista de {@link PacienteAdminResponseDTO}
     */
    @Operation(summary = "Listar pacientes (admin)", description = "Lista todos los pacientes con datos completos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/admin")
    public ResponseEntity<List<PacienteAdminResponseDTO>> findAllAdmin() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    /**
     * Obtiene un paciente específico con datos completos (vista admin).
     *
     * @param id identificador del paciente
     * @return {@link PacienteAdminResponseDTO} con los datos del paciente
     */
    @Operation(summary = "Obtener paciente (admin)", description = "Obtiene un paciente por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente recuperado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/admin/{id}")
    public ResponseEntity<PacienteAdminResponseDTO> findByIdAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pacienteService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo paciente (vista admin).
     *
     * @param request {@link PacienteRequestDTO} con los datos del paciente
     * @return {@link PacienteAdminResponseDTO} con los datos del paciente creado
     */
    @Operation(summary = "Crear paciente (admin)", description = "Crea un nuevo paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/admin")
    public ResponseEntity<PacienteAdminResponseDTO> createAdmin(@RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un paciente existente (vista admin).
     *
     * @param id      identificador del paciente
     * @param request {@link PacienteRequestDTO} con los nuevos datos
     * @return {@link PacienteAdminResponseDTO} con los datos actualizados
     */
    @Operation(summary = "Actualizar paciente (admin)", description = "Actualiza un paciente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/admin/{id}")
    public ResponseEntity<PacienteAdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody PacienteRequestDTO request) {
        try {
            return ResponseEntity.ok(pacienteService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }


}
