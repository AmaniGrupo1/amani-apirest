    package com.amani.amaniapirest.dto.dtoAdmin.response;

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
    public class DireccionAdminResponseDTO {

        /** Nombre de pila del paciente. */
        private String nombrePaciente;
        /** Apellido del paciente. */
        private String apellidoPaciente;
        /** Nombre de la calle y número. */
        private String calle;

        /** Ciudad de residencia. */
        private String ciudad;

        /** Provincia o estado. */
        private String provincia;

        /** Código postal de la zona. */
        private String codigoPostal;

        /** País de residencia. */
        private String pais;

        /** Descripción adicional o referencia de la ubicación. */
        private String descripcion;

        /** Fecha de creación de la dirección. */
        private LocalDateTime createdAt;

        /** Fecha de última actualización de la dirección. */
        private LocalDateTime updatedAt;
    }
