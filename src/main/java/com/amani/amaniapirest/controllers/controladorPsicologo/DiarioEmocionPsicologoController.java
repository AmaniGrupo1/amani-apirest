package com.amani.amaniapirest.controllers.controladorPsicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.DiarioEmocionalPsicologoDTO;
import com.amani.amaniapirest.services.psicologo.DiarioEmocionPsicologoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que permite a un psicologo consultar el diario emocional de sus pacientes.
 *
 * <p>Base path: {@code /api/diario/psicologo}.</p>
 */
@RestController
@RequestMapping("/api/diario/psicologo")
@Tag(name = "Diario Emocional (Psicologo)", description = "Consulta del diario emocional — vista psicologo")
public class DiarioEmocionPsicologoController {

    private final DiarioEmocionPsicologoService diarioService;

    public DiarioEmocionPsicologoController(DiarioEmocionPsicologoService diarioService) {
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
    public ResponseEntity<DiarioEmocionalPsicologoDTO> getById(@PathVariable Long idDiario) {
        try {
            return ResponseEntity.ok(diarioService.findById(idDiario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
