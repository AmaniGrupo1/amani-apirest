package com.amani.amaniapirest.dto.payment.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para RefundResponse.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos RefundResponse")
public class RefundResponse {

    private String status;
    private String refundId;
}
