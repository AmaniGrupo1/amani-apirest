package com.amani.amaniapirest.dto.notificacion;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para NotificacionResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos NotificacionResponseDTO")
public class NotificacionResponseDTO {
    private Long id;
    private String titulo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fecha;
}
