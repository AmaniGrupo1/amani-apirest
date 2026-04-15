package com.amani.amaniapirest.dto.dtoPsicologo.response;

import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.enums.EstadoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de respuesta para representar un paciente desde la perspectiva del psicólogo.
 *
 * <p>Contiene los datos demográficos, de contacto y de agenda del paciente,
 * ajustados para la vista del psicólogo. No expone información confidencial
 * ni el historial clínico completo.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PacientePsicologoResponse", description = "Datos básicos de un paciente — vista psicólogo")
public class PacientePsicologoResponseDTO {

    /**
     * Creates a new instance of {@code PacientePsicologoResponseDTO} with the specified parameters.
     *
     * @param idPaciente          identificador único del paciente
     * @param nombre              nombre de pila del paciente
     * @param apellido            apellido del paciente
     * @param dni                 número de documento de identidad
     * @param fechaNacimiento     fecha de nacimiento del paciente
     * @param email               dirección de correo electrónico
     * @param genero              género del paciente
     * @param telefono            número de teléfono de contacto
     * @param direccion           dirección postal del paciente
     * @param horaInicio          hora de inicio de la sesión
     * @param horaFin             hora de finalización de la sesión
     */
    public PacientePsicologoResponseDTO(Long idPaciente, String nombre, String apellido, String dni,
                                        LocalDate fechaNacimiento, String email, String genero,
                                        String telefono, DireccionResponseDTO direccion,
                                        LocalTime horaInicio, LocalTime horaFin) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.genero = genero;
        this.telefono = telefono;
        this.direccion = direccion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    private Long idPaciente;


    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")
    private String nombre;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")
    private String apellido;

    private String dni;
    /** Fecha de nacimiento del paciente. */
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate fechaNacimiento;

    private String email;
    /** Género del paciente. */
    @Schema(description = "Género", example = "femenino")
    private String genero;

    /** Número de teléfono del paciente. */
    @Schema(description = "Teléfono", example = "+34612345678")
    private String telefono;

    private DireccionResponseDTO direccion;

    private EstadoPago estadoPago;

    /** Hora de inicio de la sesión. */
    @Schema(description = "Hora de inicio", example = "09:00")
    private LocalTime horaInicio;

    /** Hora de fin de la sesión. */
    @Schema(description = "Hora de fin", example = "10:00")
    private LocalTime horaFin;

}
