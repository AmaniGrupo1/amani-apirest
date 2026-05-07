package com.amani.amaniapirest.repository.hostorialClinico;

import com.amani.amaniapirest.models.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link HistorialClinico}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas.
 * Incluye consulta para listar registros por paciente.</p>
 */
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {

    /**
     * Obtiene todos los registros del historial clinico de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de registros clinicos del paciente
     */
    List<HistorialClinico> findByPacienteIdPaciente(Long idPaciente);
    
}

