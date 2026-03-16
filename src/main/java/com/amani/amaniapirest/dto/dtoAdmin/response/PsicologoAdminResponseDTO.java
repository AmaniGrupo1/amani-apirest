package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la vista de administrador sobre un {@code Psicologo}.
 *
 * <p>Combina los datos del perfil profesional del psicólogo con los datos de
 * identidad del {@code Usuario} asociado, ofreciendo al administrador una
 * visión completa del profesional registrado.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoAdminResponseDTO {


    /** Nombre de pila del usuario vinculado. */
    private String nombreUsuario;

    /** Apellido del usuario vinculado. */
    private String apellidoUsuario;

    /** Correo electrónico del usuario vinculado. */
    private String emailUsuario;

    /** Especialidad o área de enfoque terapéutico. */
    private String especialidad;

    /** Años de experiencia profesional. */
    private Integer experiencia;

    /** Descripción del psicólogo y su enfoque. */
    private String descripcion;

    /** Número de licencia o colegiación profesional. */
    private String licencia;

    /** Fecha y hora de creación del perfil. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del perfil. */
    private LocalDateTime updatedAt;
}

