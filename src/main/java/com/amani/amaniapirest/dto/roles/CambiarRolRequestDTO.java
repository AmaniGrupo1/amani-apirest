package com.amani.amaniapirest.dto.roles;

import io.swagger.v3.oas.annotations.media.Schema;


import com.amani.amaniapirest.enums.RolUsuario;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para CambiarRolRequestDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos CambiarRolRequestDTO")
public class CambiarRolRequestDTO {
    @NotNull
    private Long idUsuario;
    @NotNull
    private RolUsuario nuevoRol;
}
