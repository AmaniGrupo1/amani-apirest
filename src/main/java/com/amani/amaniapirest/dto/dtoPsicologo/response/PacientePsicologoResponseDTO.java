package com.amani.amaniapirest.dto.dtoPsicologo.response;

import com.amani.amaniapirest.enums.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para que el psicólogo consulte los datos básicos de un {@code Paciente}.
 *
 * <p>Muestra únicamente la información demográfica y de contacto del paciente,
 * sin exponer datos de identidad completos ni historial clínico.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PacientePsicologoResponse", description = "Datos básicos de un paciente — vista psicólogo")
public class PacientePsicologoResponseDTO {

    private Long idPaciente;

    /**
     * Constructor usado por la query JPQL de {@code PsicologoPacienteRepository.obtenerPacientesActivos}.
     */
    public PacientePsicologoResponseDTO(Long idPaciente, String nombre, String apellido,
                                        LocalDate fechaNacimiento, String email,
                                        String genero, String telefono,
                                        EstadoPago estadoPago) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.genero = genero;
        this.telefono = telefono;
        this.estadoPago = estadoPago;
    }

    /** Nombre de pila del paciente. */
    @Schema(description = "Nombre del paciente", example = "Laura")
    private String nombre;

    /** Apellido del paciente. */
    @Schema(description = "Apellido del paciente", example = "Martínez")
    private String apellido;

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

    private String direccion;

    private EstadoPago estadoPago;

    /** Hora de inicio de la sesión. */
    @Schema(description = "Hora de inicio", example = "09:00")
    private LocalTime horaInicio;

    /** Hora de fin de la sesión. */
    @Schema(description = "Hora de fin", example = "10:00")
    private LocalTime horaFin;

}
