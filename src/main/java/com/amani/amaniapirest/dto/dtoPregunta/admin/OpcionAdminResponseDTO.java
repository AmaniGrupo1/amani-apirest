package com.amani.amaniapirest.dto.dtoPregunta.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpcionAdminResponseDTO {
    private Long idOpcion;
    private String texto;
    private Integer valor;
}
