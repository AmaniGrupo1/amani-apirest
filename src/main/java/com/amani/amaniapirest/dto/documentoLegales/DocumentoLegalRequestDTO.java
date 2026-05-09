package com.amani.amaniapirest.dto.documentoLegales;


import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoLegalRequestDTO {

    private TipoDocumentoLegal tipo;

    private String titulo;

    private String contenido;

    private String icono;

    private Integer ordenVisualizacion;

    private String version;

    private Boolean activo;
}
