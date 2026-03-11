package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.ProgresoEmocional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link ProgresoEmocional}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consulta para listar registros por paciente.</p>
 */
public interface ProgresoEmocionalRepository extends JpaRepository<ProgresoEmocional, Long> {

    /**
     * Obtiene todos los registros de progreso emocional de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de registros de progreso del paciente
     */
    List<ProgresoEmocional> findByPaciente_IdPaciente(Long idPaciente);
}

