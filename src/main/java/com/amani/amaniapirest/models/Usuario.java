package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.RolUsuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"psicologo", "paciente", "enviados", "recibidos", "hibernateLazyInitializer"})
@Entity
@Getter
@Setter
@ToString(exclude = {"paciente", "psicologo", "enviados", "recibidos"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    /**
     * Identificador unico del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    /**
     * Nombre de pila del usuario.
     */
    private String nombre;

    /**
     * Apellido del usuario.
     */
    private String apellido;


    private String dni;
    /**
     * Correo electronico unico utilizado para el inicio de sesion.
     */
    private String email;

    private String fotoPerfilUrl;

    /**
     * Contrasena del usuario almacenada con hash BCrypt.
     */
    private String password;

    /**
     * Rol funcional del usuario segun {@link RolUsuario}.
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "rol_usuario", nullable = false)
    private RolUsuario rol;

    /**
     * Indica si la cuenta del usuario esta activa.
     */
    private Boolean activo;

    /**
     * Fecha y hora de registro del usuario en el sistema.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** URL del avatar por defecto, servido como recurso estático por Spring Boot. */
    public static final String AVATAR_DEFAULT = "/avatar-default.svg";

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
        // Si no se asignó foto al crear el usuario, se usa el avatar por defecto
        if (this.fotoPerfilUrl == null || this.fotoPerfilUrl.isBlank()) {
            this.fotoPerfilUrl = AVATAR_DEFAULT;
        }
    }

    /**
     * Fecha y hora en que la cuenta fue dada de baja, o {@code null} si sigue activa.
     */
    private LocalDateTime fechaBaja;

    /**
     * Token de registro de Firebase Cloud Messaging (FCM) del dispositivo del usuario.
     * Se actualiza cada vez que la app móvil/web refresca su token FCM.
     * Puede ser {@code null} si el usuario no ha activado las notificaciones push.
     *
     * <p><strong>Migración BD requerida:</strong>
     * {@code ALTER TABLE usuarios ADD COLUMN fcm_token VARCHAR(512);}</p>
     */
    @Column(name = "fcm_token", length = 512)
    private String fcmToken;

    /**
     * Perfil de paciente asociado a este usuario, si aplica.
     */
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Paciente paciente;

    /**
     * Perfil de psicologo asociado a este usuario, si aplica.
     */
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Psicologo psicologo;

    /**
     * Lista de mensajes enviados por este usuario.
     */
    @OneToMany(mappedBy = "sender")
    private List<Mensaje> enviados;

    /**
     * Lista de mensajes recibidos por este usuario.
     */
    @OneToMany(mappedBy = "receiver")
    private List<Mensaje> recibidos;
}
