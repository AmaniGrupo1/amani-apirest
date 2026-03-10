package com.amani.amaniapirest.domain;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario {
    private Long id_usuario;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    //private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fecha_registro;
    private LocalDateTime fecha_baja;
}
