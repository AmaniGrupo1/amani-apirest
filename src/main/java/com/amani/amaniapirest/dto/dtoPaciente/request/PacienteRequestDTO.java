package com.amani.amaniapirest.dto.dtoPaciente.request;

import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.RespuestasRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteRequestDTO {
    private Long idUsuario; // Solo para admin, para vincular con un usuario existente
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;

    private UsuarioRequestDTO usuario;

    private List<DireccionRequestDTO> direcciones; // Solo campos necesarios
    private List<CitaRequestDTO> citas;             // Solo para admin
    private List<HistorialClinicoRequestDTO> historiales; // Admin y psicólogo
    private List<RespuestasRequestDTO> respuestas;
}
