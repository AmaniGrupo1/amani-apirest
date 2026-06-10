package com.amani.amaniapirest.repository.iaInsert;

import com.amani.amaniapirest.models.iaInsert.ChunkRag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChunkRagRepository extends JpaRepository<ChunkRag, Long> {

    // Método que te faltaba
    List<ChunkRag> findByDocumento_IdDocumento(Long documentoId);

    List<ChunkRag> findByDocumento_IdDocumentoOrderByChunkIndexAsc(Long documentoId);

}