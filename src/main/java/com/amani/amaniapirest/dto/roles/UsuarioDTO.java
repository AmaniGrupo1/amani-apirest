package com.amani.amaniapirest.dto.roles;

import io.swagger.v3.oas.annotations.media.Schema;


import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para UsuarioDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos UsuarioDTO")
public class UsuarioDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private RolUsuario rol;
    private Boolean activo;
    private Long idPsicologo;     // ✅ AÑADE esto (si aplica)
    private Long idPaciente;

}