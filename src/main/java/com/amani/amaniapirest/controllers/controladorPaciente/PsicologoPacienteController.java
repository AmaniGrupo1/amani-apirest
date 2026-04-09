package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.response.PsicologoResponseDTO;
import com.amani.amaniapirest.services.paciente.PsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que permite a un paciente consultar los psicologos disponibles.
 *
 * <p>Base path: {@code /api/paciente/psicologos}.</p>
 */
@RestController
@RequestMapping("/api/paciente/psicologos")
@Tag(name = "Psicologos (Vista Paciente)", description = "Consulta de psicologos disponibles — vista paciente")
public class PsicologoPacienteController {

    private final PsicologoService service;

    public PsicologoPacienteController(PsicologoService service) {
        this.service = service;
    }

    /**
     * Lista todos los psicologos disponibles en el sistema.
     *
     * @return lista de psicologos.
     */
    @Operation(summary = "Listar psicologos", description = "Lista todos los psicologos disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public List<PsicologoResponseDTO> getAll() {
        return service.findAll();
    }

    /**
     * Obtiene un psicologo por su identificador.
     *
     * @param id identificador del psicologo.
     * @return datos del psicologo.
     */
    @Operation(summary = "Obtener psicologo", description = "Obtiene un psicologo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public PsicologoResponseDTO getById(@PathVariable Long id) {
        return service.findById(id);
    }
}