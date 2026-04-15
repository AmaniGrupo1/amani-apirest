    package com.amani.amaniapirest.dto.dtoAdmin.response;

    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    /**
     * DTO de respuesta para la vista de administrador sobre la dirección postal de un paciente.
     *
     * <p>Incluye el nombre completo del paciente junto con todos los campos de la dirección
     * y las marcas de tiempo de auditoría.</p>
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "DireccionAdminResponse", description = "Dirección de un paciente — vista administrador")
public class DireccionAdminResponseDTO {

        /** Nombre de pila del paciente. */
        @Schema(description = "Nombre del paciente", example = "Laura")

        private String nombrePaciente;
        /** Apellido del paciente. */
        @Schema(description = "Apellido del paciente", example = "Martínez")

        private String apellidoPaciente;
        /** Nombre de la calle y número. */
        @Schema(description = "Calle y número", example = "Calle Mayor 12")

        private String calle;

        /** Ciudad de residencia. */
        @Schema(description = "Ciudad", example = "Madrid")

        private String ciudad;

        /** Provincia o estado. */
        @Schema(description = "Provincia", example = "Madrid")

        private String provincia;

        /** Código postal de la zona. */
        @Schema(description = "Código postal", example = "28001")

        private String codigoPostal;

        /** País de residencia. */
        @Schema(description = "País", example = "España")

        private String pais;

        /** Descripción adicional o referencia de la ubicación. */
        @Schema(description = "Descripción adicional", example = "Puerta 3B")

        private String descripcion;

        /** Fecha de creación de la dirección. */
        @Schema(description = "Fecha de creación", example = "2026-01-15T08:00:00")

        private LocalDateTime createdAt;

        /** Fecha de última actualización de la dirección. */
        @Schema(description = "Última actualización", example = "2026-03-20T10:30:00")

        private LocalDateTime updatedAt;
    }
