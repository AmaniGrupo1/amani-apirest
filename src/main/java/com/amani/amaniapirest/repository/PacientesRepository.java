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
    @Query("SELECT p FROM Paciente p JOIN FETCH p.usuario u WHERE u.idUsuario = :idUsuario")
    Optional<Paciente> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("SELECT p.idPaciente FROM Paciente p WHERE p.psicologo.idPsicologo = :idPsicologo")
    List<Long> findIdsByPsicologoId(@Param("idPsicologo") Long idPsicologo);
}
