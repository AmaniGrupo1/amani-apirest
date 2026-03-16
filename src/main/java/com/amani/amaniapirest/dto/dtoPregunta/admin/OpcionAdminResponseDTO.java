package com.amani.amaniapirest.dto.dtoPregunta.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpcionAdminResponseDTO {
    private String texto;
    private String tipo;
    private List<String> opciones;
    private LocalDateTime creadoEn;
}
