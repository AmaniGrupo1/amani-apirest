package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Paciente}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas
 * y consultas por convencion de nombres sin necesidad de implementacion manual.</p>
 */
public interface PacientesRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByUsuario_IdUsuario(Long idUsuario);

    @Query("""
                SELECT DISTINCT p FROM Paciente p
                LEFT JOIN FETCH p.pacienteSituaciones ps
                LEFT JOIN FETCH ps.situacion
                LEFT JOIN FETCH p.tutores t
                LEFT JOIN FETCH p.direcciones d
            """)
    List<Paciente> findAllWithSituacionesTutoresYDirecciones();

    @Query("""
                SELECT p.idPaciente FROM Paciente p
                WHERE p.psicologo.idPsicologo = :idPsicologo
            """)
    List<Long> findIdsByPsicologoId(Long idPsicologo);


    // Consulta personalizada para obtener un paciente con su psicólogo y usuario asociados
    @Query("""
            SELECT pa FROM Paciente pa
            LEFT JOIN FETCH pa.psicologo p
            LEFT JOIN FETCH p.usuario u
            WHERE pa.idPaciente = :idPaciente
            """)
    Optional<Paciente> findPacienteWithPsicologo(@Param("idPaciente") Long idPaciente);


    Optional<Paciente> findByUsuario_Email(String email);

    @Query("""
                SELECT DISTINCT p
                FROM Paciente p
                LEFT JOIN FETCH p.direcciones
                LEFT JOIN FETCH p.tutores
                LEFT JOIN FETCH p.usuario
                WHERE NOT EXISTS (
                    SELECT pp
                    FROM PsicologoPaciente pp
                    WHERE pp.paciente = p
                    AND pp.fechaFin IS NULL
                )
            """)
    List<Paciente> findPacientesSinPsicologo();
}
