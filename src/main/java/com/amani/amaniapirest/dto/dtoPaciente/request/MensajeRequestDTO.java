package com.amani.amaniapirest.dto.dtoPaciente.request;

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
public class MensajeRequestDTO {

    /** Identificador del usuario que envía el mensaje. */
    @NotNull
    private Long idSender;

    /** Identificador del usuario que recibe el mensaje. */
    @NotNull
    private Long idReceiver;

    /** Contenido textual del mensaje. */
    @NotBlank
    private String mensaje;

    /** Identificador de la cita relacionada, si aplica. */
    private Long idCita;
}
