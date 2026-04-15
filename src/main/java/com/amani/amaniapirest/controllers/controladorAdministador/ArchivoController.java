package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.request.ArchivoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.ArchivoResponseDTO;
import com.amani.amaniapirest.services.paciente.ArchivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de archivos adjuntos a sesiones.
 *
 * <p>Base path: {@code /api/archivos}. Permite listar, obtener, descargar, subir y eliminar archivos.</p>
 */
@RestController
@RequestMapping("/api/archivos")
@Tag(name = "Archivos", description = "Gestión de archivos adjuntos a sesiones")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    /**
     * Lista todos los archivos del sistema.
     *
     * @return lista de archivos
     */
    @Operation(summary = "Listar archivos", description = "Obtiene la lista de todos los archivos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay archivos registrados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ArchivoResponseDTO>> findAll() {
        return ResponseEntity.ok(archivoService.findAll());
    }

    /**
     * Obtiene los metadatos de un archivo por su identificador.
     *
     * @param id identificador del archivo
     * @return los metadatos del archivo o 404 si no existe
     */
    @Operation(summary = "Obtener archivo", description = "Obtiene la informacion de un archivo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArchivoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(archivoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista los archivos adjuntos a una sesión.
     *
     * @param idSesion identificador de la sesion
     * @return lista de archivos de la sesión
     */
    @Operation(summary = "Archivos por sesion", description = "Obtiene la lista de archivos adjuntos a una sesion especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay archivos para esta sesión", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/sesion/{idSesion}")
    public ResponseEntity<List<ArchivoResponseDTO>> findBySesion(@PathVariable Long idSesion) {
        return ResponseEntity.ok(archivoService.findBySesion(idSesion));
    }

    /**
     * Descarga el contenido binario de un archivo.
     *
     * @param id identificador del archivo
     * @return el contenido binario del archivo con sus metadatos
     */
    @Operation(summary = "Descargar archivo", description = "Descarga el contenido binario de un archivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        try {
            ArchivoResponseDTO meta = archivoService.findById(id);
            byte[] datos = archivoService.getBytes(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + meta.getNombre() + "\"")
                    .contentType(MediaType.parseMediaType(
                            meta.getTipoMime() != null ? meta.getTipoMime() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(datos);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Sube un nuevo archivo adjunto a una sesión (contenido en Base64).
     *
     * @param request datos del archivo a subir
     * @return el archivo recien creado
     */
    @Operation(summary = "Subir archivo", description = "Sube un nuevo archivo adjunto (contenido en Base64)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ArchivoResponseDTO> create(@RequestBody ArchivoRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(archivoService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina un archivo por su identificador.
     *
     * @param id identificador del archivo a eliminar
     * @return 204 No Content si se elimino correctamente, 404 si no existe
     */
    @Operation(summary = "Eliminar archivo", description = "Elimina un archivo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            archivoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
