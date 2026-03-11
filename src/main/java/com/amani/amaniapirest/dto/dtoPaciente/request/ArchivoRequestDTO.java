package com.amani.amaniapirest.dto.request;

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
public class ArchivoRequestDTO {

    /** Identificador de la sesión a la que se adjunta el archivo. */
    @NotNull
    private Long idSesion;

    /** Nombre del archivo incluyendo extensión. */
    @NotBlank
    private String nombre;

    /** Tipo MIME del archivo (p.ej. "application/pdf", "image/png"). */
    @NotBlank
    private String tipoMime;

    /** Contenido del archivo codificado en Base64. */
    private String datosBase64;
}
