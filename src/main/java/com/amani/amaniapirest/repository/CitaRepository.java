package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPsicologo_IdPsicologo(Long idPsicologo);

    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    List<Cita> findByPaciente_IdPacienteAndStartDatetimeBetween(
            Long idPaciente,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    List<Cita> findByPsicologo_IdPsicologoAndStartDatetimeBetween(
            Long idPsicologo,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    List<Cita> findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
            Long idPsicologo,
            LocalDateTime desde,
            LocalDateTime hasta,
            EstadoCita estado
    );



    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, EstadoCita name);
}
