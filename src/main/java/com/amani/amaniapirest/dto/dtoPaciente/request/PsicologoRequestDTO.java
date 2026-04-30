package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String especialidad;

    private Integer experiencia;

    private String descripcion;

    private String licencia;

}
