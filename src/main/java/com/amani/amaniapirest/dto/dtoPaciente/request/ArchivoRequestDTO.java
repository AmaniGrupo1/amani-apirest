package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para subir un archivo adjunto a una sesión.
 *
 * <p>Los campos {@code idSesion}, {@code nombre} y {@code tipoMime} son
 * obligatorios. El contenido binario {@code datos} se recibe en Base64
 * y se decodifica en el servicio antes de persistir.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ArchivoRequest", description = "Datos para subir un archivo adjunto a una sesión (contenido en Base64)")
public class ArchivoRequestDTO {

    /** Identificador de la sesión a la que se adjunta el archivo. */
    @NotNull
    @Schema(description = "Identificador de la sesión a la que se adjunta el archivo", example = "5")
    private Long idSesion;

    /** Nombre del archivo incluyendo extensión. */
    @NotBlank
    @Schema(description = "Nombre del archivo incluyendo extensión", example = "informe.pdf")
    private String nombre;

    /** Tipo MIME del archivo (p.ej. "application/pdf", "image/png"). */
    @NotBlank
    @Schema(description = "Tipo MIME del archivo", example = "application/pdf")
    private String tipoMime;

    /** Contenido del archivo codificado en Base64. */
    @Schema(description = "Contenido del archivo codificado en Base64", example = "JVBERi0xLjQK...")
    private String datosBase64;
}
