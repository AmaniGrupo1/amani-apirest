package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la gestión de direcciones postales asociadas a los pacientes.
 *
 * <p>Un paciente puede tener registradas múltiples direcciones (domicilio habitual,
 * dirección de facturación, etc.). Extiende {@link JpaRepository} para proveer
 * operaciones CRUD estándar.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    /**
     * Recupera todas las direcciones registradas para un paciente concreto.
     *
     * <p>Permite obtener el listado completo de domicilios del paciente, utilizado
     * en la gestión del perfil y en procesos de facturación.</p>
     *
     * @param idPaciente Identificador único del paciente.
     * @return Lista de {@link Direccion} del paciente; lista vacía si no tiene direcciones registradas.
     */
    List<Direccion> findByPaciente_IdPaciente(Long idPaciente);
}

