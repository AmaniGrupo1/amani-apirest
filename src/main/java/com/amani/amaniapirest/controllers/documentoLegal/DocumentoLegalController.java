package com.amani.amaniapirest.controllers.documentoLegal;


import com.amani.amaniapirest.dto.documentoLegales.DocumentoLegalRequestDTO;
import com.amani.amaniapirest.dto.documentoLegales.DocumentoLegalResponseDTO;
import com.amani.amaniapirest.services.documentoLegal.DocumentoLegalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos-legales")
@RequiredArgsConstructor
public class DocumentoLegalController {

    private final DocumentoLegalService documentoLegalService;

    // =========================
    // CREAR
    // =========================
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
    @GetMapping
    public List<DocumentoLegalResponseDTO> obtenerTodosDocumentoLegales() {

        return documentoLegalService.obtenerTodos();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @GetMapping("/{idDocumento}")
    public DocumentoLegalResponseDTO obtenerPorId(
            @PathVariable Long idDocumento
    ) {

        return documentoLegalService.obtenerPorId(idDocumento);
    }

    // =========================
    // EDITAR
    // =========================
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
    @DeleteMapping("/delete/{idDocumento}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDocumento(
            @PathVariable Long idDocumento
    ) {
        documentoLegalService.eliminarDocumento(idDocumento);
    }
}
