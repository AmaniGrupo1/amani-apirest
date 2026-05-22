package com.amani.amaniapirest.dto.profile.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para AdminResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos AdminResponseDTO")
public class AdminResponseDTO {
    private AdminDTO admin;
    private String token;
}
