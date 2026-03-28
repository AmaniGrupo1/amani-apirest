package com.amani.amaniapirest.dto.dtoPaciente.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
