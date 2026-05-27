package com.amani.amaniapirest.dto.dtoAdmin.response;

import io.swagger.v3.oas.annotations.media.Schema;

import com.amani.amaniapirest.dto.dtoAdmin.TutorResonseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para PacienteAdminResponseDTO.
 * 
 * Representa los datos de transferencia para la operación correspondiente.
 */
@Schema(description = "Objeto de transferencia de datos PacienteAdminResponseDTO")
public class PacienteAdminResponseDTO {

    private Long idPaciente;

    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;

    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean activo;
    private String estadoPago;
    private String metodoPago;
    private List<SituacionDTO> situaciones;
    private List<TutorResonseDTO> tutores;
    private List<DireccionResponseDTO> direcciones;
    private Integer edad;
}

