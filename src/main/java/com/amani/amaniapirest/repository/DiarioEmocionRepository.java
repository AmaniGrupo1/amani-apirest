package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.DiarioEmocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la gestión de entradas del diario emocional de los pacientes.
 *
 * <p>El diario emocional permite a los pacientes registrar su estado anímico y reflexiones
 * entre sesiones. Este repositorio facilita la recuperación de dichas entradas para su
 * revisión por parte del paciente o del psicólogo asignado. Extiende {@link JpaRepository}
 * para proveer operaciones CRUD estándar.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface DiarioEmocionRepository extends JpaRepository<DiarioEmocion, Long> {

    /**
     * Recupera todas las entradas del diario emocional registradas por un paciente concreto.
     *
     * <p>Permite al paciente consultar su historial emocional completo y al psicólogo
     * analizar la evolución anímica del paciente entre sesiones.</p>
     *
     * @param idPaciente Identificador único del paciente autor de las entradas.
     * @return Lista de {@link DiarioEmocion} del paciente; lista vacía si no tiene entradas.
     */
    List<DiarioEmocion> findByPaciente_IdPaciente(Long idPaciente);
}

