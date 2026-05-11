package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Usuario}.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Comprueba si ya existe un usuario con el email indicado (para evitar duplicados). */
    boolean existsByEmail(String email);

    /** Busca un usuario por su email (usado en autenticación y en el DataInitializer). */
    Optional<Usuario> findByEmail(String email);

    boolean existsByRol(RolUsuario attr0);

    List<Usuario> findByRol(RolUsuario rol); // 👈 ESTE

    /** Buscar usuarios cuyo DNI contiene la cadena dada. */
    List<Usuario> findByDniContaining(String dni);

    /** Buscar usuarios por rol y cuyo DNI contiene la cadena dada. */
    List<Usuario> findByRolAndDniContaining(RolUsuario rol, String dni);

    @Query("""
        SELECT u.fcmToken
        FROM Usuario u
        WHERE u.idUsuario = :idUsuario
    """)
    String obtenerToken(Long idUsuario);


    @Query("""
    SELECT u FROM Usuario u
    WHERE (:rol IS NULL OR u.rol = :rol)
    AND (:dni IS NULL OR u.dni LIKE %:dni%)
""")
    List<Usuario> filtrarUsuarios(
            @Param("rol") RolUsuario rol,
            @Param("dni") String dni
    );

}
