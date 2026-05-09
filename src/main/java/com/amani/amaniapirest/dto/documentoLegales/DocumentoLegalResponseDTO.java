package com.amani.amaniapirest.dto.documentoLegales;


import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoLegalResponseDTO {

    private Long idDocumento;

    private TipoDocumentoLegal tipo;

    private String titulo;

    private String contenido;

    private String icono;

    private Integer ordenVisualizacion;

    private String version;

    private Boolean activo;

    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;
}
