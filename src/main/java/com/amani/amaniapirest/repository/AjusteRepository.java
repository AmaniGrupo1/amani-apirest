package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Ajuste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para la gestión de ajustes de configuración personal de cada usuario.
 *
 * <p>Cada usuario dispone de un único registro de ajustes que personaliza su experiencia
 * en la plataforma (preferencias de notificación, idioma, accesibilidad, etc.).
 * Extiende {@link JpaRepository} para proveer operaciones CRUD estándar.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface AjusteRepository extends JpaRepository<Ajuste, Long> {

    /**
     * Recupera los ajustes de configuración asociados a un usuario concreto.
     *
     * <p>Se utiliza para cargar las preferencias del usuario autenticado al iniciar sesión
     * o al consultar su perfil de configuración.</p>
     *
     * @param idUsuario Identificador único del usuario propietario de los ajustes.
     * @return {@link Optional} con los ajustes del usuario, o vacío si el usuario aún
     *         no tiene ajustes registrados.
     */
    Optional<Ajuste> findByUsuario_IdUsuario(Long idUsuario);

}
