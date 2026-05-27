package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Consentimiento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de acceso a datos para la gestión de consentimientos informados firmados por los pacientes.
 *
 * <p>Un consentimiento informado es el documento legal que el paciente acepta antes de iniciar
 * el proceso terapéutico. Este repositorio provee las operaciones CRUD estándar heredadas de
 * {@link JpaRepository} para su almacenamiento y recuperación.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface ConsentimientoRepository extends JpaRepository<Consentimiento,Long> {
}
