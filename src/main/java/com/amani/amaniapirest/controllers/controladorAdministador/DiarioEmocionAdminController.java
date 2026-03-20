package com.amani.amaniapirest.controllers.controladorAdministador;


import com.amani.amaniapirest.dto.dtoAdmin.response.DiarioEmocionAdminResponseDTO;
import com.amani.amaniapirest.services.serviceAdmin.DiarioEmocionAdminService;
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
 * Controlador REST de administracion para consultar entradas del diario emocional.
 *
 * <p>Base path: {@code /api/diario/admin}. Accesible solo por usuarios con rol admin.</p>
 */
@RestController
@RequestMapping("/api/diario/admin")
@Tag(name = "Diario Emocional (Admin)", description = "Consulta del diario emocional — vista administrador")
public class DiarioEmocionAdminController {

    private final DiarioEmocionAdminService diarioService;

    public DiarioEmocionAdminController(DiarioEmocionAdminService diarioService) {
        this.diarioService = diarioService;
    }

    /**
     * Obtiene una entrada del diario emocional por su identificador.
     *
     * @param idDiario identificador de la entrada.
     * @return la entrada encontrada o 404 si no existe.
     */
    @Operation(summary = "Obtener entrada de diario", description = "Obtiene una entrada del diario emocional por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{idDiario}")
    public ResponseEntity<DiarioEmocionAdminResponseDTO> getById(@PathVariable Long idDiario) {
        try {
            return ResponseEntity.ok(diarioService.findById(idDiario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}