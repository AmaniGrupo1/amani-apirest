package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;

import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteRequestDTO {
    private Long idUsuario;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
    private EstadoPago estadoPago; // "PENDIENTE" / "PAGADO"
    private MetodoPago metodoPago; // "PRESENCIAL" / "ONLINE"
    private UsuarioRequestDTO usuario;
    private List<Long> idSituaciones;
    @NotNull(message = "Debe aceptar los términos y condiciones")
    private Boolean  aceptaTerminos;
    private Boolean  aceptaVideoconferencia;
    private Boolean  aceptaComunicacion;
    private List<TutorRequestDTO> tutores;
    private List<DireccionRequestDTO> direccion;
}
