package com.amani.amaniapirest.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePsicologoRequestDTO {
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String licencia;

    private UsuarioUpdateDTO usuario;
}
