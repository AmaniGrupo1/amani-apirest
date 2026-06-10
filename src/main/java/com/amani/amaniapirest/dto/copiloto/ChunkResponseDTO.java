package com.amani.amaniapirest.dto.copiloto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChunkResponseDTO {
    private Long idChunk;
    private Integer chunkIndex;
    private String contenido;

    // Información del documento
    private Long documentoId;
    private String nombreDocumento;

    private LocalDateTime creadoEn;
}
