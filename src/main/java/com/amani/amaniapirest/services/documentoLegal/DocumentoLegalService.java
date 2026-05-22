package com.amani.amaniapirest.services.documentoLegal;

import com.amani.amaniapirest.dto.documentoLegales.DocumentoLegalRequestDTO;
import com.amani.amaniapirest.dto.documentoLegales.DocumentoLegalResponseDTO;
import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import com.amani.amaniapirest.models.DocumentoLegal;
import com.amani.amaniapirest.repository.DocumentoLegalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio DocumentoLegalService.
 *
 * Proporciona la lógica de negocio asociada a DocumentoLegalService.
 */
@Service
@RequiredArgsConstructor
public class DocumentoLegalService {

    private final DocumentoLegalRepository documentoLegalRepository;

    // =========================
    // CREAR
    // =========================
    /**
     * Método crearDocumento.
     *
     * @return el resultado de la operación
     */
    public DocumentoLegalResponseDTO crearDocumento(
            DocumentoLegalRequestDTO request
    ) {

        DocumentoLegal documento = DocumentoLegal.builder()
                .tipo(request.getTipo())
                .titulo(request.getTitulo())
                .contenido(request.getContenido())
                .icono(request.getIcono())
                .ordenVisualizacion(request.getOrdenVisualizacion())
                .version(request.getVersion())
                .activo(request.getActivo())
                .build();

        documentoLegalRepository.save(documento);

        return mapToResponse(documento);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    /**
     * Método obtenerTodos.
     *
     * @return el resultado de la operación
     */
    public List<DocumentoLegalResponseDTO> obtenerTodos() {

        return documentoLegalRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    /**
     * Método obtenerPorId.
     *
     * @return el resultado de la operación
     */
    public DocumentoLegalResponseDTO obtenerPorId(
            Long idDocumento
    ) {
        DocumentoLegal documento = documentoLegalRepository.findById(idDocumento)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Documento legal no encontrado"
                        )
                );

        return mapToResponse(documento);
    }

    // =========================
    // EDITAR
    // =========================
    /**
     * Método editarDocumento.
     *
     * @return el resultado de la operación
     */
    public DocumentoLegalResponseDTO editarDocumento(
            Long idDocumento,
            DocumentoLegalRequestDTO request
    ) {

        DocumentoLegal documento = documentoLegalRepository.findById(idDocumento)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Documento legal no encontrado"
                        )
                );

        documento.setTipo(request.getTipo());
        documento.setTitulo(request.getTitulo());
        documento.setContenido(request.getContenido());
        documento.setIcono(request.getIcono());
        documento.setOrdenVisualizacion(request.getOrdenVisualizacion());
        documento.setVersion(request.getVersion());
        documento.setActivo(request.getActivo());
        documento.setActualizadoEn(LocalDateTime.now());

        documentoLegalRepository.save(documento);

        return mapToResponse(documento);
    }

    // =========================
    // ELIMINAR
    // =========================
    /**
     * Método eliminarDocumento.
     *
     * @return el resultado de la operación
     */
    public void eliminarDocumento(
            Long idDocumento
    ) {

        DocumentoLegal documento = documentoLegalRepository.findById(idDocumento)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Documento legal no encontrado"
                        )
                );

        documentoLegalRepository.delete(documento);
    }

    // =========================
    // MAPEO RESPONSE
    // =========================
    private DocumentoLegalResponseDTO mapToResponse(
            DocumentoLegal documento
    ) {

        return DocumentoLegalResponseDTO.builder()
                .idDocumento(documento.getIdDocumento())
                .tipo(documento.getTipo())
                .titulo(documento.getTitulo())
                .contenido(documento.getContenido())
                .icono(documento.getIcono())
                .ordenVisualizacion(documento.getOrdenVisualizacion())
                .version(documento.getVersion())
                .activo(documento.getActivo())
                .creadoEn(documento.getCreadoEn())
                .actualizadoEn(documento.getActualizadoEn())
                .build();
    }


    /**
     * Método obtenerPorTipo.
     *
     * @return el resultado de la operación
     */
    public DocumentoLegalResponseDTO obtenerPorTipo(
            TipoDocumentoLegal tipo
    ) {

        DocumentoLegal documento = documentoLegalRepository
                .findByTipoAndActivoTrue(tipo)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Documento legal no encontrado"
                        )
                );

        return mapToResponse(documento);
    }
}