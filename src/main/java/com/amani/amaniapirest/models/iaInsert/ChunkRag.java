    package com.amani.amaniapirest.models.iaInsert;



    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "chunks_rag")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ChunkRag {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_chunk")
        private Long idChunk;

        @ManyToOne
        @JoinColumn(name = "id_documento", nullable = false)
        @JsonBackReference
        private DocumentoRag documento;

        @Column(name = "chunk_index")
        private Integer chunkIndex;

        @Column(columnDefinition = "TEXT")
        private String contenido;

        @Column(name = "embedding_id", columnDefinition = "TEXT")
        @JsonIgnore
        private String embeddingId;

        @Column(name = "creado_en")
        private LocalDateTime creadoEn = LocalDateTime.now();

    }
