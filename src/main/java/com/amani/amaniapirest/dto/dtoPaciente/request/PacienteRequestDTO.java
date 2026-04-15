package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para la solicitud de creación o actualización de un paciente.
 *
 * <p>Utilizado para registrar o modificar la información de un paciente en el sistema,
 * incluyendo sus datos personales, situación administrativa y preferencias de tratamiento.</p>
 *
 * @param idUsuario               identificador único del usuario asociado
 * @param fechaNacimiento         fecha de nacimiento del paciente
 * @param genero                  género del paciente
 * @param telefono                número de teléfono de contacto
 * @param estadoPago              estado del pago (PENDIENTE o PAGADO)
 * @param metodoPago              método de pago (PRESENCIAL o ONLINE)
 * @param usuario                 datos del usuario asociado
 * @param idSituaciones           lista de identificadores de situaciones asociadas
 * @param aceptaTerminos          indica si el paciente acepta los términos y condiciones
 * @param aceptaVideoconferencia  indica si el paciente acepta sesiones por videoconferencia
 * @param aceptaComunicacion      indica si el paciente acepta comunicación por email o SMS
 * @param tutores                 lista de tutores del paciente (menores de edad)
 * @param direccion               lista de direcciones del paciente
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteRequestDTO {
    private Long idUsuario;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
    private EstadoPago estadoPago; // "PENDIENTE" / "PAGADO"
    private MetodoPago metodoPago; // "PRESENCIAL" / "ONLINE"
    private UsuarioRequestDTO usuario;
    private List<Long> idSituaciones;
    @NotNull(message = "Debe aceptar los términos y condiciones")
    private Boolean  aceptaTerminos;
    private Boolean  aceptaVideoconferencia;
    private Boolean  aceptaComunicacion;
    private List<TutorRequestDTO> tutores;
    private List<DireccionRequestDTO> direccion;
}
