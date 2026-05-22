package com.amani.amaniapirest.controllers.documentoLegal;


import com.amani.amaniapirest.dto.documentoLegales.DocumentoLegalRequestDTO;
import com.amani.amaniapirest.dto.documentoLegales.DocumentoLegalResponseDTO;
import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import com.amani.amaniapirest.services.documentoLegal.DocumentoLegalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de documentos legales.
 */
@RestController
@RequestMapping("/api/documentos-legales")
@RequiredArgsConstructor
@Tag(name = "Documentos Legales", description = "API para gestionar los documentos legales del sistema")
public class DocumentoLegalController {

    private final DocumentoLegalService documentoLegalService;

    // =========================
    // CREAR
    // =========================
    @Operation(summary = "Crear documento legal", description = "Crea un nuevo documento legal en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Documento legal creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    })
    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoLegalResponseDTO crearDocumento(
            @RequestBody DocumentoLegalRequestDTO request
    ) {

        return documentoLegalService.crearDocumento(request);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @Operation(summary = "Listar todos los documentos legales", description = "Obtiene una lista de todos los documentos legales disponibles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de documentos legales obtenida exitosamente")
    })
    @GetMapping
    public List<DocumentoLegalResponseDTO> obtenerTodosDocumentoLegales() {

        return documentoLegalService.obtenerTodos();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @Operation(summary = "Obtener documento legal por ID", description = "Obtiene los detalles de un documento legal específico mediante su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento legal obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Documento legal no encontrado")
    })
    @GetMapping("/{idDocumento}")
    public DocumentoLegalResponseDTO obtenerPorId(
            @PathVariable Long idDocumento
    ) {

        return documentoLegalService.obtenerPorId(idDocumento);
    }

    // =========================
    // EDITAR
    // =========================
    @Operation(summary = "Editar documento legal", description = "Actualiza la información de un documento legal existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento legal actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "404", description = "Documento legal no encontrado")
    })
    @PutMapping("/{idDocumento}")
    public DocumentoLegalResponseDTO editarDocumento(
            @PathVariable Long idDocumento,
            @RequestBody DocumentoLegalRequestDTO request
    ) {

        return documentoLegalService.editarDocumento(
                idDocumento,
                request
        );
    }

    // =========================
    // ELIMINAR
    // =========================
    @Operation(summary = "Eliminar documento legal", description = "Elimina un documento legal del sistema mediante su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Documento legal eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Documento legal no encontrado")
    })
    @DeleteMapping("/delete/{idDocumento}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDocumento(
            @PathVariable Long idDocumento
    ) {
        documentoLegalService.eliminarDocumento(idDocumento);
    }

    @Operation(summary = "Obtener documento legal por tipo", description = "Obtiene un documento legal específico basado en su tipo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento legal obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Documento legal no encontrado para el tipo especificado")
    })
    @GetMapping("/tipo/{tipo}")
    public DocumentoLegalResponseDTO obtenerPorTipo(
            @PathVariable TipoDocumentoLegal tipo
    ) {
        return documentoLegalService.obtenerPorTipo(tipo);
    }
}
