package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Sesion}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD básicas
 * y consultas por convención de nombres sin necesidad de implementación manual.</p>
 */
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    /**
     * Obtiene todas las sesiones realizadas por un psicólogo.
     *
     * @param idPsicologo identificador del psicólogo
     * @return lista de sesiones del psicólogo indicado
     */
    List<Sesion> findByIdPsicologo(Long idPsicologo);

    /**
     * Obtiene todas las sesiones de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de sesiones del paciente indicado
     */
    List<Sesion> findByIdPaciente(Long idPaciente);
}
