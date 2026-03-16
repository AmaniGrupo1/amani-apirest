package com.amani.amaniapirest.controllers.controladorAdministador;

import com.amani.amaniapirest.dto.dtoPaciente.request.ArchivoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.ArchivoResponseDTO;
import com.amani.amaniapirest.services.paciente.ArchivoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de archivos adjuntos de sesiones.
 *
 * <p>Base URL: {@code /api/archivos}</p>
 */
@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    /** GET /api/archivos — Lista todos los archivos. */
    @GetMapping
    public ResponseEntity<List<ArchivoResponseDTO>> findAll() {
        return ResponseEntity.ok(archivoService.findAll());
    }

    /** GET /api/archivos/{id} — Obtiene un archivo por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ArchivoResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(archivoService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/archivos/sesion/{idSesion} — Lista los archivos de una sesión. */
    @GetMapping("/sesion/{idSesion}")
    public ResponseEntity<List<ArchivoResponseDTO>> findBySesion(@PathVariable Long idSesion) {
        return ResponseEntity.ok(archivoService.findBySesion(idSesion));
    }

    /**
     * GET /api/archivos/{id}/download — Descarga el contenido binario de un archivo.
     * Devuelve los bytes con el tipo MIME original del archivo.
     */
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

    /** POST /api/archivos — Sube un nuevo archivo adjunto a una sesión (contenido en Base64). */
    @PostMapping
    public ResponseEntity<ArchivoResponseDTO> create(@RequestBody ArchivoRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(archivoService.create(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** DELETE /api/archivos/{id} — Elimina un archivo. */
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

