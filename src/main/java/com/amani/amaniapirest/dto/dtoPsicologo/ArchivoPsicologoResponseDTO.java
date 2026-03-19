package com.amani.amaniapirest.dto.dtoPsicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para que el psicólogo consulte los archivos adjuntos de una sesión.
 *
 * <p>Incluye únicamente los metadatos del archivo (nombre y tipo MIME) sin el
 * contenido binario, para mantener respuestas ligeras.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivoPsicologoResponseDTO {

    /** Nombre original del archivo incluyendo extensión. */
    private String nombre;

    /** Tipo MIME del archivo (p.ej. "application/pdf", "image/png"). */
    private String tipoMime;
}
