package com.amani.amaniapirest.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TutorResonseDTO {
    private String nombre;
    private String telefono;
    private String email;
    private String dni;
    private String tipo;
}
