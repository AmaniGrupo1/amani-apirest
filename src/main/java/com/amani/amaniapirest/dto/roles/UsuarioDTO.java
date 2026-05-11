package com.amani.amaniapirest.dto.roles;


import com.amani.amaniapirest.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private RolUsuario rol;
    private Boolean activo;
    private Long idPsicologo;     // ✅ AÑADE esto (si aplica)
    private Long idPaciente;

}