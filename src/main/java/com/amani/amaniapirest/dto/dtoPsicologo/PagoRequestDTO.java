package com.amani.amaniapirest.dto.dtoPsicologo;

import io.swagger.v3.oas.annotations.media.Schema;

import com.amani.amaniapirest.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para PagoRequestDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos PagoRequestDTO")
public class PagoRequestDTO {

    @NotNull
    private Long idCita;

    @NotNull
    private MetodoPago metodoPago;

    @NotNull
    private BigDecimal monto;
}
