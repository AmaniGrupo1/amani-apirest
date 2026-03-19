package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Paciente}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas
 * y consultas por convencion de nombres sin necesidad de implementacion manual.</p>
 */
public interface PacientesRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByUsuario_IdUsuario(Long idUsuario);
}
