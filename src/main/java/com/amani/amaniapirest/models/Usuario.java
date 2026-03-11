package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad base de autenticación e identidad de los usuarios del sistema.
 *
 * <p>Centraliza datos de acceso (email y contraseña hasheada) y perfil basico.
 * Según el {@link RolUsuario} asignado, el usuario puede estar vinculado a un
 * perfil de {@link Paciente} o de {@link Psicologo}.</p>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    /** Identificador unico del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    /** Nombre de pila del usuario. */
    private String nombre;

    /** Apellido del usuario. */
    private String apellido;

    /** Correo electronico unico utilizado para el inicio de sesion. */
    private String email;

    /** Contrasena del usuario almacenada con hash BCrypt. */
    private String password;

    /** Rol funcional del usuario segun {@link RolUsuario}. */
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    /** Indica si la cuenta del usuario esta activa. */
    private Boolean activo;

    /** Fecha y hora de registro del usuario en el sistema. */
    private LocalDateTime fechaRegistro;

    /** Fecha y hora en que la cuenta fue dada de baja, o {@code null} si sigue activa. */
    private LocalDateTime fechaBaja;

    /** Perfil de paciente asociado a este usuario, si aplica. */
    @OneToOne(mappedBy = "usuario")
    private Paciente paciente;

    /** Perfil de psicologo asociado a este usuario, si aplica. */
    @OneToOne(mappedBy = "usuario")
    private Psicologo psicologo;

    /** Lista de mensajes enviados por este usuario. */
    @OneToMany(mappedBy = "sender")
    private List<Mensaje> enviados;

    /** Lista de mensajes recibidos por este usuario. */
    @OneToMany(mappedBy = "receiver")
    private List<Mensaje> recibidos;
}
