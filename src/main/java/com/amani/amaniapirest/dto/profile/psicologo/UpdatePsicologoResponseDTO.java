package com.amani.amaniapirest.dto.profile.psicologo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para UpdatePsicologoResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos UpdatePsicologoResponseDTO")
public class UpdatePsicologoResponseDTO {
    private PsicologoDTO psicologo;
    private String token;
}
