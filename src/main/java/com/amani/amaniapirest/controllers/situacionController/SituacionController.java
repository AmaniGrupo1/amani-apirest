package com.amani.amaniapirest.controllers.situacionController;

import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.repository.SituacionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de situaciones emocionales.
 *
 * <p>Expone endpoints para consultar las situaciones disponibles que pueden
 * afectar la salud emocional de los pacientes.</p>
 */
@RestController
@RequestMapping("/api/situaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Situaciones", description = "Gestión de situaciones psicosociales")
public class SituacionController {

    private final SituacionRepository situacionRepository;

    /**
     * Lista todas las situaciones activas disponibles.
     *
     * @return lista de {@link SituacionDTO}
     */
    @Operation(summary = "Listar situaciones", description = "Obtiene la lista de todas las situaciones psicosociales activas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<SituacionDTO>> listarSituaciones() {
        List<Situacion> situaciones = situacionRepository.findByActivoTrue();

        List<SituacionDTO> response = situaciones.stream()
                .map(s -> new SituacionDTO(
                        s.getIdSituacion(),
                        s.getNombre(),
                        s.getCategoria(),
                        s.getDescripcion()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una situación específica por su ID.
     *
     * @param id identificador de la situación
     * @return {@link SituacionDTO} con los datos de la situación
     */
    @Operation(summary = "Obtener situación por ID", description = "Recupera los detalles de una situación específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Situación recuperada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Situación no encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SituacionDTO> obtenerSituacion(@PathVariable Long id) {
        Situacion situacion = situacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Situación no encontrada"));

        SituacionDTO response = new SituacionDTO(
                situacion.getIdSituacion(),
                situacion.getNombre(),
                situacion.getCategoria(),
                situacion.getDescripcion()
        );

        return ResponseEntity.ok(response);
    }
}
