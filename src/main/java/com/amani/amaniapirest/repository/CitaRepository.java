package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Cita}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas
 * y consultas por convencion de nombres sin necesidad de implementacion manual.</p>
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {
}
