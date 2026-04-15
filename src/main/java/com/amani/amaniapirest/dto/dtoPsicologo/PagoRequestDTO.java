package com.amani.amaniapirest.dto.dtoPsicologo;

import com.amani.amaniapirest.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PagoRequestDTO {

    @NotNull
    private Long idCita;

    @NotNull
    private MetodoPago metodoPago;

    @NotNull
    private BigDecimal monto;
}
