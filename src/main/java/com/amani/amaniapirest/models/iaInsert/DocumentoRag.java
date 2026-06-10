    package com.amani.amaniapirest.models.iaInsert;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
    import java.util.List;

    @Entity
    @Table(name = "documentos_rag")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DocumentoRag {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_documento")
        private Long idDocumento;

        private String titulo;

        private String categoria;

        private String fuente;

        @Column(name = "nombre_archivo")
        private String nombreArchivo;

        @Column(name = "total_chunks")
        private Integer totalChunks = 0;

        @Column(name = "creado_en")
        private LocalDateTime creadoEn = LocalDateTime.now();

        @OneToMany(
                mappedBy = "documento",
                cascade = CascadeType.ALL,
                orphanRemoval = true
        )
        @JsonManagedReference
        private List<ChunkRag> chunks;
    }
