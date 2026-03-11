package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Psicologo}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas
 * y consultas por convencion de nombres sin necesidad de implementacion manual.</p>
 */
public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
}