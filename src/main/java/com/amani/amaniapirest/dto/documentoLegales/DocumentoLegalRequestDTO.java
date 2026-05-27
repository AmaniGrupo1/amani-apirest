package com.amani.amaniapirest.dto.documentoLegales;

import io.swagger.v3.oas.annotations.media.Schema;


import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * DTO para DocumentoLegalRequestDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos DocumentoLegalRequestDTO")
public class DocumentoLegalRequestDTO {

    private TipoDocumentoLegal tipo;

    private String titulo;

    private String contenido;

    private String icono;

    private Integer ordenVisualizacion;

    private String version;

    private Boolean activo;
}
