package com.amani.amaniapirest.dto.profile.psicologo;

import io.swagger.v3.oas.annotations.media.Schema;

import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para PsicologoDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos PsicologoDTO")
public class PsicologoDTO {

    private Long idPsicologo;
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String licencia;
    private UsuarioDTO usuario;
}
