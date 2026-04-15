package com.amani.amaniapirest.dto.dtoPaciente.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de creación o actualización de un tutor.
 *
 * <p>Utilizado para transferir los datos de un tutor desde el cliente hacia el servidor
 * en operaciones relacionadas con la gestión de pacientes.</p>
 *
 * @param nombre    nombre completo del tutor
 * @param telefono  número de teléfono de contacto
 * @param email     dirección de correo electrónico
 * @param dni       número de documento de identidad
 * @param tipo      tipo de tutor (MADRE, PADRE o TUTOR)
 * @see PacienteRequestDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorRequestDTO {
    private String nombre;
    private String telefono;
    private String email;
    private String dni;
    private String tipo; // MADRE / PADRE / TUTOR
}
