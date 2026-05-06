package com.amani.amaniapirest.dto.profile.psicologo;

import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoDTO {

    private Long idPsicologo;
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String licencia;
    private UsuarioDTO usuario;
}
