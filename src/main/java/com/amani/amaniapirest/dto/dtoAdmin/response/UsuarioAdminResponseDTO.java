    package com.amani.amaniapirest.dto.dtoAdmin.response;

    import com.amani.amaniapirest.enums.RolUsuario;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

    /**
     * DTO de respuesta para la vista de administrador sobre un {@code Usuario}.
     *
     * <p>Expone todos los campos del usuario excepto la contraseña, incluyendo
     * datos de auditoría como fecha de registro y fecha de baja.</p>
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "UsuarioAdminResponse", description = "Datos completos de un usuario — vista administrador")
public class UsuarioAdminResponseDTO {

        /** Identificador único del usuario. */
        @Schema(description = "Identificador del usuario", example = "1")

        private Long idUsuario;

        /** Nombre de pila del usuario. */
        @Schema(description = "Nombre de pila", example = "Carlos")

        private String nombre;

        /** Apellido del usuario. */
        @Schema(description = "Apellido", example = "López")

        private String apellido;

        /** Correo electrónico único del usuario. */
        @Schema(description = "Correo electrónico", example = "carlos@amani.com")

        private String email;

        /** Rol funcional del usuario en el sistema. */
        @Schema(description = "Rol del usuario", example = "paciente")

        private RolUsuario rol;

        /** Indica si la cuenta está activa. */
        @Schema(description = "Si la cuenta está activa", example = "true")

        private Boolean activo;

        /** Fecha y hora en que el usuario fue registrado. */
        @Schema(description = "Fecha de registro", example = "2026-01-15T08:00:00")

        private LocalDateTime fechaRegistro;

        /** Fecha y hora en que la cuenta fue dada de baja, o {@code null} si sigue activa. */
        @Schema(description = "Fecha de baja o null si activa", example = "null")

        private LocalDateTime fechaBaja;
    }

