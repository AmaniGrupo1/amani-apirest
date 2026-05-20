package com.amani.amaniapirest.dto.roles;

import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarRolResponseDTO {

    private Long idUsuario;

    private String nombre;

    private String email;

    private RolUsuario rolAnterior;

    private RolUsuario nuevoRol;

    private String mensaje;

    // NUEVO TOKEN
    private String token;

    // NUEVOS IDS
    private Long idPsicologo;

    private Long idPaciente;

    // AJUSTES
    private String idioma;

    private Boolean tema;
}
