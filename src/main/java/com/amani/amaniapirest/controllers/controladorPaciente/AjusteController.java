package com.amani.amaniapirest.controllers.controladorPaciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.AjusteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AjusteResponseDTO;
import com.amani.amaniapirest.services.paciente.AjusteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ajustes")
@Tag(name = "Ajustes", description = "Gestion de ajustes de usuario")
public class AjusteController {

    private final AjusteService ajusteService;

    public AjusteController(AjusteService ajusteService) {
        this.ajusteService = ajusteService;
    }

    /**
     * Lista todos los ajustes del sistema.
     *
     * @return lista de ajustes
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Listar ajustes", description = "Recupera todos los ajustes del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajustes retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<AjusteResponseDTO>> findAll() {
        return ResponseEntity.ok(ajusteService.findAll());
    }

    /**
     * Obtiene un ajuste por su identificador único.
     *
     * @param id identificador único del ajuste
     * @return datos del ajuste
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Obtener ajuste", description = "Recupera un ajuste específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajuste retornado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún ajuste con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AjusteResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ajusteService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene los ajustes de un usuario específico.
     *
     * @param idUsuario identificador único del usuario
     * @return ajustes del usuario
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Ajustes por usuario", description = "Recupera los ajustes de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajustes del usuario retornados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún usuario con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<AjusteResponseDTO> findByUsuario(@PathVariable Long idUsuario) {
        try {
            return ResponseEntity.ok(ajusteService.findByUsuario(idUsuario));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea nuevos ajustes para un usuario.
     *
     * @param request datos para crear los ajustes
     * @return ajustes creados
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Crear ajuste", description = "Crea nuevos ajustes para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ajustes creados correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AjusteResponseDTO> create(@Valid @RequestBody AjusteRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ajusteService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza los ajustes de un usuario.
     *
     * @param id      identificador único del ajuste a actualizar
     * @param request nuevos datos de los ajustes
     * @return ajustes actualizados
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Actualizar ajuste", description = "Actualiza los ajustes de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajustes actualizados correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún ajuste con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AjusteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AjusteRequestDTO request) {
        try {
            return ResponseEntity.ok(ajusteService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un ajuste por su identificador único.
     *
     * @param id identificador único del ajuste a eliminar
     * @throws RuntimeException si ocurre un error en la capa de servicio
     */
    @Operation(summary = "Eliminar ajuste", description = "Elimina un ajuste por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ajuste eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún ajuste con el ID especificado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            ajusteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
