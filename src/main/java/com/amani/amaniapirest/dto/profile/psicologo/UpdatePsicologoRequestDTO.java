package com.amani.amaniapirest.dto.profile.psicologo;

import io.swagger.v3.oas.annotations.media.Schema;

import com.amani.amaniapirest.dto.profile.paciente.UsuarioUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para UpdatePsicologoRequestDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos UpdatePsicologoRequestDTO")
public class UpdatePsicologoRequestDTO {
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String licencia;

    private UsuarioUpdateDTO usuario;
}
