package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Situacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Situacion}.
 */
public interface SituacionRepository extends JpaRepository<Situacion, Long> {

    List<Situacion> findByActivoTrue();
}
