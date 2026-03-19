package com.amani.amaniapirest.dto.dtoAdmin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoAdminResponseDTO {

    private String nombrePsicologo;

    private String apellidoPsicologo;

    private String nombreUsuario;

    private String apellidoUsuario;

    private String emailUsuario;

    private LocalDateTime updatedAt;
}

