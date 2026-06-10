package com.amani.amaniapirest.dto.copiloto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
   private Long documentoId;
   private String mensaje;
}
