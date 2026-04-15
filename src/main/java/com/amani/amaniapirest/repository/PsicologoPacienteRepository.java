package com.amani.amaniapirest.repository;


import com.amani.amaniapirest.models.PsicologoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PsicologoPacienteRepository extends JpaRepository<PsicologoPaciente, Long> {

    // Pacientes activos de un psicólogo
    List<PsicologoPaciente> findByPsicologoIdPsicologoAndFechaFinIsNull(Long idPsicologo);

    // Asignación activa de un paciente
    PsicologoPaciente findByPacienteIdPacienteAndFechaFinIsNull(Long idPaciente);

    Optional<PsicologoPaciente> findByPaciente_Usuario_IdUsuario(Long idUsuario);

    @Query("""
            SELECT pp FROM PsicologoPaciente pp
            JOIN FETCH pp.paciente p
            LEFT JOIN FETCH p.tutores
            WHERE pp.psicologo.idPsicologo = :idPsicologo
            AND pp.fechaFin IS NULL
            """)
    List<PsicologoPaciente> findPacientesConTutores(Long idPsicologo);
}
