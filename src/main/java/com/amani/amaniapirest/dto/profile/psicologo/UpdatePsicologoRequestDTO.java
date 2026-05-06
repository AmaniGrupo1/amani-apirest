package com.amani.amaniapirest.dto.profile.psicologo;

import com.amani.amaniapirest.dto.profile.paciente.UsuarioUpdateDTO;
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
