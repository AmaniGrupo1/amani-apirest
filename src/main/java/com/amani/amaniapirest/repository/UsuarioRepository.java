package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Usuario}.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
<<<<<<< HEAD

    /** Comprueba si ya existe un usuario con el email indicado (para evitar duplicados). */
    boolean existsByEmail(String email);

    /** Busca un usuario por su email (usado en autenticación y en el DataInitializer). */
=======
>>>>>>> origin/main
    Optional<Usuario> findByEmail(String email);
}
