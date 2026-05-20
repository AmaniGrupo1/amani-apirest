package com.amani.amaniapirest.repository;


import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PsicologoPacienteRepository extends JpaRepository<PsicologoPaciente, Long> {

    // Pacientes activos de un psicólogo
    List<PsicologoPaciente> findByPsicologoIdPsicologoAndFechaFinIsNull(Long idPsicologo);

    // Asignación activa de un paciente
    Optional<PsicologoPaciente> findByPacienteIdPacienteAndFechaFinIsNull(Long idPaciente);

    Optional<PsicologoPaciente> findByPaciente_Usuario_IdUsuario(Long idUsuario);

    Optional<PsicologoPaciente> findByPsicologo_Usuario_IdUsuario(Long idUsuario);

    @Query(value = """
            SELECT pp FROM PsicologoPaciente pp
            JOIN FETCH pp.paciente p
            LEFT JOIN FETCH p.tutores
            WHERE pp.psicologo.idPsicologo = :idPsicologo
            AND pp.fechaFin IS NULL
            """)
    List<PsicologoPaciente> findPacientesConTutores(Long idPsicologo);

    boolean existsByPsicologo_IdPsicologoAndPaciente_IdPacienteAndFechaFinIsNull(
            Long idPsicologo,
            Long idPaciente
    );


    List<PsicologoPaciente> findByPsicologo_IdPsicologoAndFechaFinIsNull(Long idPsicologo);


    @Modifying
    @Query("""
        DELETE FROM PsicologoPaciente pp
        WHERE pp.paciente.idPaciente = :idPaciente
    """)
    void deleteByPacienteId(@Param("idPaciente") Long idPaciente);

    @Modifying
    @Query("""
    DELETE FROM PsicologoPaciente pp
    WHERE pp.psicologo.idPsicologo = :idPsicologo
""")
    void deleteByPsicologo_IdPsicologo(
            @Param("idPsicologo") Long idPsicologo
    );


    boolean existsByPsicologo_IdPsicologo(Long idPsicologo);

    boolean existsByPaciente_IdPaciente(Long idPaciente);
}
