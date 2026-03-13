package com.amani.amaniapirest.dto.dtoPregunta.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreguntaAdminResponseDTO {
    private String texto;
    private String tipo;
    private List<OpcionAdminResponseDTO> opciones;
}
