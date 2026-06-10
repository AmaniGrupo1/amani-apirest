package com.amani.amaniapirest.configuration.ia;


import com.amani.amaniapirest.models.iaInsert.ChunkRag;
import com.amani.amaniapirest.models.iaInsert.DocumentoRag;
import com.amani.amaniapirest.repository.iaInsert.ChunkRagRepository;
import com.amani.amaniapirest.repository.iaInsert.DocumentoRepository;
import com.amani.amaniapirest.services.iaInsert.EmbeddingService;
import com.amani.amaniapirest.services.iaInsert.TextChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final DocumentoRepository documentoRepo;
    private final ChunkRagRepository chunkRepo;
    private final TextChunkService chunkService;
    private final EmbeddingService embeddingService;



    public void ingest(Long documentoId, String text) {

        System.out.println("PASO 1");

        DocumentoRag doc = documentoRepo.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        System.out.println("PASO 2");

        List<String> chunks = chunkService.split(text, 500, 50);

        System.out.println("PASO 3 - chunks: " + chunks.size());

        List<ChunkRag> entities = new ArrayList<>();

        int index = 0;

        for (String chunk : chunks) {

            System.out.println("PASO 4 - chunk " + index);

            List<Double> embedding = embeddingService.getEmbedding(chunk);

            System.out.println("PASO 5 - embedding generado");

            ChunkRag entity = new ChunkRag();

            entity.setDocumento(doc);
            entity.setChunkIndex(index++);
            entity.setContenido(chunk);
            entity.setEmbeddingId(embedding.toString());

            entities.add(entity);
        }

        System.out.println("PASO 6");

        chunkRepo.saveAll(entities);

        System.out.println("PASO 7");

        doc.setTotalChunks(entities.size());

        documentoRepo.save(doc);

        System.out.println("PASO 8");
    }
}
