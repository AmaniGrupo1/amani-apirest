package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Direccion}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consulta para listar direcciones por paciente.</p>
 */
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    /**
     * Obtiene todas las direcciones registradas para un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de direcciones del paciente
     */
    List<Direccion> findByPaciente_IdPaciente(Long idPaciente);
}

