package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.RolUsuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad central de identidad y autenticación de la plataforma.
 *
 * <p>Concentra los datos comunes a todos los perfiles del sistema (paciente, psicólogo
 * y administrador): credenciales de acceso, rol, estado de la cuenta y token FCM para
 * notificaciones push. Cada {@link Paciente} o {@link Psicologo} se vincula a un
 * Usuario mediante una relación 1:1.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
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
     * Identificador único del usuario.
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

    /** Documento Nacional de Identidad del usuario. */
    private String dni;

    /**
     * Correo electrónico único utilizado para el inicio de sesión.
     */
    private String email;

    /** URL de la foto de perfil del usuario. Si no se asigna, se usa {@link #AVATAR_DEFAULT}. */
    private String fotoPerfilUrl;

    /**
     * Contraseña del usuario almacenada con hash BCrypt.
     */
    private String password;

    /**
     * Rol funcional del usuario según {@link RolUsuario}.
     */
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, length = 50)
    private RolUsuario rol;

    /**
     * Indica si la cuenta del usuario está activa.
     */
    private Boolean activo;

    /**
     * Fecha y hora de registro del usuario en el sistema.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** URL del avatar por defecto, servido como recurso estático por Spring Boot. */
    public static final String AVATAR_DEFAULT = "/avatar-default.svg";

    /**
     * Inicializa los campos de auditoría al persistir el registro por primera vez.
     *
     * <p>Establece la fecha de registro, activa la cuenta y asigna el avatar por defecto
     * si no se proporcionó foto de perfil.</p>
     */
    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
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
    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    /**
     * Perfil de paciente asociado a este usuario, si aplica.
     */
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Paciente paciente;

    /**
     * Perfil de psicólogo asociado a este usuario, si aplica.
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

    /** Lista de notificaciones push dirigidas a este usuario. */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones;

    /** Indica si el usuario tiene habilitada la recepción de notificaciones push. */
    @Column(name = "notificaciones_activas")
    private boolean notificacionesActivas = true;
}
