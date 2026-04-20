    package com.amani.amaniapirest.dto.dtoAgenda.response;

    import com.amani.amaniapirest.dto.terapiasDTO.TerapiaResponseDTO;
    import com.amani.amaniapirest.enums.EstadoCita;
    import com.amani.amaniapirest.enums.EstadoPago;
    import com.amani.amaniapirest.enums.MetodoPago;
    import com.amani.amaniapirest.enums.ModalidadCita;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.time.LocalTime;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class AgendaItemDTO {
        private Long id;
        private Long idPaciente;
        private LocalDate fecha;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private String tipo;
        private String estado;
        private String motivo;
        private Integer duracionMinutos;
        private String nombrePaciente;
        private String nombrePsicologo;
        private TerapiaResponseDTO terapia;
        private MetodoPago metodoPago;
        private EstadoPago estadoPago;
        private ModalidadCita modalidad;
    }
