package com.amani.amaniapirest.dto.dtoPsicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte los archivos adjuntos de una sesión.
 *
 * <p>Incluye únicamente los metadatos del archivo (nombre y tipo MIME) sin el
 * contenido binario, para mantener respuestas ligeras.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ArchivoPsicologoResponse", description = "Metadatos de un archivo — vista psicólogo")
public class ArchivoPsicologoResponseDTO {

    /** Nombre original del archivo incluyendo extensión. */
    @Schema(description = "Nombre del archivo", example = "informe.pdf")

    private String nombre;

    /** Tipo MIME del archivo (p.ej. "application/pdf", "image/png"). */
    @Schema(description = "Tipo MIME", example = "application/pdf")

    private String tipoMime;
}
