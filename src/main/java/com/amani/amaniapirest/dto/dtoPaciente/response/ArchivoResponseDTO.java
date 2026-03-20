package com.amani.amaniapirest.dto.dtoPaciente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida con la información de un archivo adjunto.
 *
 * <p>No incluye el contenido binario para evitar respuestas excesivamente
 * grandes. Para descargar el archivo se debe usar un endpoint específico.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ArchivoResponse", description = "Metadatos de un archivo adjunto")
public class ArchivoResponseDTO {

    /** Identificador único del archivo. */
    @Schema(description = "Identificador único del archivo", example = "1")

    private Long idArchivo;

    /** Identificador de la sesión a la que pertenece el archivo. */
    @Schema(description = "Identificador de la sesión asociada", example = "3")

    private Long idSesion;

    /** Nombre original del archivo incluyendo extensión. */
    @Schema(description = "Nombre del archivo", example = "informe.pdf")

    private String nombre;

    /** Tipo MIME del archivo. */
    @Schema(description = "Tipo MIME del archivo", example = "application/pdf")

    private String tipoMime;

    /** Fecha y hora en que el archivo fue creado o subido. */
    @Schema(description = "Fecha de subida del archivo", example = "2026-03-20T10:30:00")

    private LocalDateTime creadoEn;
}
