package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Usuario}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas
 * y consultas por convención de nombres sin necesidad de implementación manual.</p>
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
