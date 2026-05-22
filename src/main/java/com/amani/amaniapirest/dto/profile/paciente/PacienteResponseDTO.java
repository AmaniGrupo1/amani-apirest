package com.amani.amaniapirest.dto.profile.paciente;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para PacienteResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos PacienteResponseDTO")
public class PacienteResponseDTO {
    private PacienteDTO paciente;
    private String token;
}
