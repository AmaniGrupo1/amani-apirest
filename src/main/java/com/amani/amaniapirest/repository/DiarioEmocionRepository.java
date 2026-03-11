package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.DiarioEmocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link DiarioEmocion}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consulta para listar entradas por paciente.</p>
 */
public interface DiarioEmocionRepository extends JpaRepository<DiarioEmocion, Long> {

    /**
     * Obtiene todas las entradas del diario emocional de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de entradas del diario del paciente
     */
    List<DiarioEmocion> findByPaciente_IdPaciente(Long idPaciente);
}

