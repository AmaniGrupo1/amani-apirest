package com.amani.amaniapirest.dto.payment.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para CreatePaymentIntentRequest.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos CreatePaymentIntentRequest")
public class CreatePaymentIntentRequest {

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;
}
