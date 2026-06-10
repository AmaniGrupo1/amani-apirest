package com.amani.amaniapirest.services.iaInsert;

import com.amani.amaniapirest.models.iaInsert.ChunkRag;
import com.amani.amaniapirest.models.iaInsert.DocumentoRag;
import com.amani.amaniapirest.repository.iaInsert.ChunkRagRepository;
import com.amani.amaniapirest.repository.iaInsert.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagIngestionService {

    private final ChunkRagRepository chunkRepo;
    private final DocumentoRepository documentoRepository;
    private final TextChunkService chunkService;
    private final EmbeddingService embeddingService;

    public void processDocument(Long idDocumento, String text) {
        // Usar chunking semántico mejorado
        List<String> chunks = chunkService.splitSemantic(text);

        System.out.println("Documento dividido en " + chunks.size() + " chunks");

        int index = 0;
        for (String chunk : chunks) {
            // Opcional: enriquecer chunk con metadatos
            String enrichedChunk = enrichChunk(chunk, index, chunks.size());

            List<Double> embedding = embeddingService.getEmbedding(enrichedChunk);

            ChunkRag entity = new ChunkRag();
            DocumentoRag docRef = documentoRepository.findById(idDocumento)
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + idDocumento));

            entity.setDocumento(docRef);
            entity.setChunkIndex(index++);
            entity.setContenido(enrichedChunk);
            entity.setEmbeddingId(embedding.toString());

            chunkRepo.save(entity);

            // Log para debugging
            System.out.println("Chunk " + index + " longitud: " + enrichedChunk.length() + " caracteres");
        }

        // Actualizar total de chunks en documento
        DocumentoRag doc = documentoRepository.findById(idDocumento).orElse(null);
        if (doc != null) {
            doc.setTotalChunks(chunks.size());
            documentoRepository.save(doc);
        }
    }

    /**
     * Enriquece el chunk con contexto adicional (título del documento, índice, etc.)
     */
    private String enrichChunk(String chunk, int index, int total) {
        // Añadir contexto posicional
        StringBuilder enriched = new StringBuilder();
        enriched.append("[Parte ").append(index + 1).append("/").append(total).append("] ");
        enriched.append(chunk);
        return enriched.toString();
    }
}