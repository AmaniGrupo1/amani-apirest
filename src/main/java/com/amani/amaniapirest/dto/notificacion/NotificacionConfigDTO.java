package com.amani.amaniapirest.dto.notificacion;

import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para NotificacionConfigDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos NotificacionConfigDTO")
public class NotificacionConfigDTO {

    private Long idUsuario;
    private boolean notificacionesActivas;
}