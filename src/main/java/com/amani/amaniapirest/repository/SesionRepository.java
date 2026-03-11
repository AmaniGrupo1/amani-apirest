package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Sesion}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD básicas
 * y consultas por convención de nombres sin necesidad de implementación manual.</p>
 */
public interface SesionRepository extends JpaRepository<Sesion, Long> {
}
