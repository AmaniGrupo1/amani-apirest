package com.amani.amaniapirest.dto.dtoPaciente.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
