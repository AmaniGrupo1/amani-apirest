package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            """)
    List<Paciente> findAllWithSituacionesAndTutores();
}
