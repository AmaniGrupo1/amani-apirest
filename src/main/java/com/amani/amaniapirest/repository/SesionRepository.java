package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Sesion}.
 */
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    /** Busca las sesiones de un paciente. @param idPaciente identificador del paciente. @return lista de sesiones. */
    List<Sesion> findByPaciente_IdPaciente(Long idPaciente);

    /** Busca las sesiones gestionadas por un psicologo. @param idPsicologo identificador del psicologo. @return lista de sesiones. */
    List<Sesion> findByPsicologo_IdPsicologo(Long idPsicologo);
}
