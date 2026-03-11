package com.amani.amaniapirest.dto.dtoPsicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientePsicologoDTO {
    private Long idPaciente;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
}