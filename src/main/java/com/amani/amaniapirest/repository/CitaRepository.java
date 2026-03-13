package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Cita}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD básicas
 * y consultas por convención de nombres sin necesidad de implementación manual.</p>
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Obtiene todas las citas asignadas a un psicólogo.
     *
     * @param idPsicologo identificador del psicólogo
     * @return lista de citas del psicólogo indicado
     */
    List<Cita> findByPsicologoIdPsicologo(Long idPsicologo);

    /**
     * Obtiene todas las citas de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de citas del paciente indicado
     */
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);
}
