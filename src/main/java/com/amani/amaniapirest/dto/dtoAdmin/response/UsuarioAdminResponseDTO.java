    package com.amani.amaniapirest.dto.dtoAdmin.response;

    import com.amani.amaniapirest.enums.RolUsuario;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    /**
     * DTO de respuesta para la vista de administrador sobre un {@code Usuario}.
     *
     * <p>Expone todos los campos del usuario excepto la contraseña, incluyendo
     * datos de auditoría como fecha de registro y fecha de baja.</p>
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class UsuarioAdminResponseDTO {

        /** Identificador único del usuario. */
        private Long idUsuario;

        /** Nombre de pila del usuario. */
        private String nombre;

        /** Apellido del usuario. */
        private String apellido;

        /** Correo electrónico único del usuario. */
        private String email;

        /** Rol funcional del usuario en el sistema. */
        private RolUsuario rol;

        /** Indica si la cuenta está activa. */
        private Boolean activo;

        /** Fecha y hora en que el usuario fue registrado. */
        private LocalDateTime fechaRegistro;

        /** Fecha y hora en que la cuenta fue dada de baja, o {@code null} si sigue activa. */
        private LocalDateTime fechaBaja;
    }

