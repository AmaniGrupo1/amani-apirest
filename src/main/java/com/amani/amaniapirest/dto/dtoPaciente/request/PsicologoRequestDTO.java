package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la solicitud de creación o registro de un psicólogo.
 *
 * <p>Utilizado para registrar nuevos psicólogos en el sistema, incluyendo
 * sus datos personales, credenciales profesionales y especialización.</p>
 *
 * @param nombrePsicologo     nombre del psicólogo
 * @param apellidoPsicologo   apellido del psicólogo
 * @param email               dirección de correo electrónico única
 * @param password            contraseña para el acceso al sistema
 * @param especialidad        área de especialización profesional
 * @param experiencia         años de experiencia profesional
 * @param descripcion         descripción profesional y enfoque de trabajo
 * @param licencia            número de licencia profesional
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoRequestDTO {

    @NotBlank
    private String nombrePsicologo;
    @NotBlank
    private String apellidoPsicologo;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Pattern(
            regexp = "^[a-z][a-z0-9._%+-]*@[a-z0-9.-]+\\.[a-z]{2,}$",
            message = "El email debe empezar en minúscula"
    )
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Debe contener letras y números"
    )
    private String password;

    @NotBlank
    private String especialidad;

    private Integer experiencia;

    private String descripcion;

    private String licencia;

}
