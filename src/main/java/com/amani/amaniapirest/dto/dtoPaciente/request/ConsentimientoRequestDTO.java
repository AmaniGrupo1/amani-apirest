package com.amani.amaniapirest.dto.dtoPaciente.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimientoRequestDTO {
    private boolean aceptaTerminos;
    private boolean aceptaVideoconferencia;
    private boolean aceptaComunicacion;
}
