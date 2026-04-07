package com.amani.amaniapirest.repository;


import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.models.PsicologoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PsicologoPacienteRepository extends JpaRepository<PsicologoPaciente, Long> {

    // Pacientes activos de un psicólogo
    List<PsicologoPaciente> findByPsicologoIdPsicologoAndFechaFinIsNull(Long idPsicologo);

    // Asignación activa de un paciente
    PsicologoPaciente findByPacienteIdPacienteAndFechaFinIsNull(Long idPaciente);

    @Query("""
    SELECT new com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO(
        p.idPaciente,
        u.nombre,
        u.apellido,
        p.fechaNacimiento,
        u.email,
        p.genero,
        p.telefono,
        p.estadoPago
    )
    FROM PsicologoPaciente pp
    JOIN pp.paciente p
    JOIN p.usuario u
    WHERE pp.psicologo.idPsicologo = :idPsicologo
    AND pp.fechaFin IS NULL
""")
    List<PacientePsicologoResponseDTO> obtenerPacientesActivos(@Param("idPsicologo") Long idPsicologo);
}
