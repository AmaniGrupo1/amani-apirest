package com.amani.amaniapirest.repository;


import com.amani.amaniapirest.models.PsicologoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PsicologoPacienteRepository extends JpaRepository<PsicologoPaciente, Long> {

    // Pacientes activos de un psicólogo
    List<PsicologoPaciente> findByPsicologoIdPsicologoAndFechaFinIsNull(Long idPsicologo);

    // Asignación activa de un paciente
    PsicologoPaciente findByPacienteIdPacienteAndFechaFinIsNull(Long idPaciente);
}
