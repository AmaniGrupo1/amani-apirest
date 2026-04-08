package com.amani.amaniapirest.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
