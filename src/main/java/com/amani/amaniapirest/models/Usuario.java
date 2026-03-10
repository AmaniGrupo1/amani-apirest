package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table (name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaBaja;
    @OneToOne(mappedBy = "usuario")
    private Paciente paciente;
    @OneToOne(mappedBy = "usuario")
    private Psicologo psicologo;
    @OneToMany(mappedBy = "sender")
    private List<Mensaje> enviados;

    @OneToMany(mappedBy = "receiver")
    private List<Mensaje> recibidos;

}
