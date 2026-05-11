package com.amani.amaniapirest.dto.roles;


import com.amani.amaniapirest.enums.RolUsuario;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarRolRequestDTO {
    @NotNull
    private Long idUsuario;
    @NotNull
    private RolUsuario nuevoRol;
}
