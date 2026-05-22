package com.amani.amaniapirest.dto.payment.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para PaymentIntentResponse.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos PaymentIntentResponse")
public class PaymentIntentResponse {

    private String clientSecret;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
}
