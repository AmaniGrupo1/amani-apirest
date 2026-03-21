package com.amani.amaniapirest.dto.dtoPaciente.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para enviar un mensaje entre dos usuarios.
 *
 * <p>Los campos {@code idSender}, {@code idReceiver} y {@code mensaje}
 * son obligatorios. El campo {@code idCita} es opcional.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MensajeRequest", description = "Datos para enviar un mensaje entre dos usuarios")
public class MensajeRequestDTO {

    /** Identificador del usuario que envía el mensaje. */
    @NotNull
    @Schema(description = "Identificador del usuario que envía el mensaje", example = "1")
    private Long idSender;

    /** Identificador del usuario que recibe el mensaje. */
    @NotNull
    @Schema(description = "Identificador del usuario que recibe el mensaje", example = "2")
    private Long idReceiver;

    /** Contenido textual del mensaje. */
    @NotBlank
    @Schema(description = "Contenido textual del mensaje", example = "Hola, ¿cómo te encuentras hoy?")
    private String mensaje;

    /** Identificador de la cita relacionada, si aplica. */
    @Schema(description = "Identificador de la cita relacionada, si aplica", example = "5")
    private Long idCita;
}
