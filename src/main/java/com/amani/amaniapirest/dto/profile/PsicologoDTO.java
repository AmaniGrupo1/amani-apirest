package com.amani.amaniapirest.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoDTO {

    private Long id;

    private String especialidad;

    private String licencia;

    private Integer experiencia;

    private String descripcion;

    private List<Long> pacientes;
}
