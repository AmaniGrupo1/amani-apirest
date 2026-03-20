package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Cita}.
 *
 * <p>Incluye consultas derivadas para buscar citas por psicologo, paciente,
 * rango de fechas y una query personalizada para obtener relaciones paciente-psicologo.</p>
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /** Busca todas las citas asignadas a un psicologo. @param idPsicologo identificador del psicologo. @return lista de citas. */
    List<Cita> findByPsicologoIdPsicologo(Long idPsicologo);

    /** Busca todas las citas de un paciente. @param idPaciente identificador del paciente. @return lista de citas. */
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    /** Busca citas en un rango de fechas con un estado dado. @param desde inicio del rango. @param hasta fin del rango. @param estado estado de la cita. @return lista de citas. */
    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, String estado);

    /**
     * Obtiene las relaciones paciente-psicologo proyectadas como {@link PsicologoAdminResponseDTO}.
     *
     * @return lista de DTOs con datos del psicologo y su paciente asociado.
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
}
