package com.amani.amaniapirest.dto.profile.psicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePsicologoResponseDTO {
    private PsicologoDTO psicologo;
    private String token;
}
