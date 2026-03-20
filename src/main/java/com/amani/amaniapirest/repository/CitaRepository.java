package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Cita}.
 *
 * <p>Extiende {@link JpaRepository} para proveer operaciones CRUD basicas
 * y consultas derivadas del nombre de metodo, ademas de una consulta JPQL
 * personalizada para obtener la relacion paciente-psicologo.</p>
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Obtiene todas las citas asignadas a un psicologo concreto.
     *
     * @param idPsicologo identificador del psicologo
     * @return lista de citas del psicologo; vacia si no tiene ninguna
     */
    List<Cita> findByPsicologo_IdPsicologo(Long idPsicologo);

    /**
     * Obtiene todas las citas de un paciente concreto.
     *
     * @param idPaciente identificador del paciente
     * @return lista de citas del paciente; vacia si no tiene ninguna
     */
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    /**
     * Busca citas cuya fecha de inicio se encuentre en un rango y tengan un estado concreto (String).
     *
     * @param desde  limite inferior del rango de fecha/hora (inclusive)
     * @param hasta  limite superior del rango de fecha/hora (inclusive)
     * @param estado estado de la cita como texto
     * @return lista de citas que cumplen los criterios
     */
    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, String estado);

    /**
     * Proyeccion JPQL que devuelve la relacion paciente-psicologo
     * en formato {@link PsicologoAdminResponseDTO} para la vista de administracion.
     *
     * @return lista de DTOs con nombre/apellido del psicologo y datos del paciente
     */
    @Query("""
                SELECT new com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO(
                    psi.usuario.nombre, psi.usuario.apellido,
                    pac.usuario.nombre, pac.usuario.apellido,
                    pac.usuario.email, pac.usuario.fechaRegistro
                )
                FROM Cita c
                JOIN c.psicologo psi
                JOIN c.paciente pac
            """)
    List<PsicologoAdminResponseDTO> getPacientesConPsicologo();

    /**
     * Busca citas cuya fecha de inicio se encuentre en un rango y tengan un {@link EstadoCita} concreto.
     *
     * @param desde  limite inferior del rango de fecha/hora (inclusive)
     * @param hasta  limite superior del rango de fecha/hora (inclusive)
     * @param name   estado de la cita como enum {@link EstadoCita}
     * @return lista de citas que cumplen los criterios
     */
    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, EstadoCita name);
}
