package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link Cita}.
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /** Obtiene todas las citas asignadas a un psicólogo. */
    List<Cita> findByPsicologo_IdPsicologo(Long idPsicologo);

    /** Obtiene todas las citas de un paciente. */
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    /**
     * Busca citas con {@code startDatetime} en la ventana [{@code desde}, {@code hasta}]
     * y con el estado indicado. Usado por {@code CitaRecordatorioScheduler} para
     * localizar citas confirmadas en las próximas ~24 h.
     *
     * @param desde  límite inferior de la ventana temporal
     * @param hasta  límite superior de la ventana temporal
     * @param estado estado de la cita (normalmente {@code "confirmada"})
     * @return lista de citas que coinciden con los criterios
     */
    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde,
                                                    LocalDateTime hasta,
                                                    String estado);
}


