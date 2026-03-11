package com.amani.amaniapirest.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoAdminDTO {
    private Long idPsicologo;
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String licencia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
