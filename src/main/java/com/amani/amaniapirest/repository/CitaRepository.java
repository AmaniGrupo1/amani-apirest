package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Repositorio de acceso a datos para la gestión de citas entre pacientes y psicólogos.
 *
 * <p>Centraliza las consultas relacionadas con el ciclo de vida de una cita: búsqueda por
 * participante, filtrado por rango temporal, filtrado por estado y carga completa con todas
 * sus relaciones para vistas de historial administrativo. Extiende {@link JpaRepository}
 * para proveer operaciones CRUD estándar.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Recupera todas las citas en las que participa un psicólogo concreto,
     * independientemente de su estado o fecha.
     *
     * @param idPsicologo Identificador único del psicólogo.
     * @return Lista de {@link Cita} del psicólogo indicado; lista vacía si no tiene citas.
     */
    List<Cita> findByPsicologo_IdPsicologo(Long idPsicologo);

    /**
     * Recupera todas las citas de un paciente ordenadas cronológicamente por fecha de inicio
     * ascendente, útil para mostrar el historial completo al propio paciente.
     *
     * @param idPaciente Identificador único del paciente.
     * @return Lista de {@link Cita} del paciente ordenadas de más antigua a más reciente.
     */
    List<Cita> findByPaciente_IdPacienteOrderByStartDatetimeAsc(Long idPaciente);

    /**
     * Recupera las citas de un paciente comprendidas dentro de un rango de fechas,
     * sin restricción de estado.
     *
     * @param idPaciente Identificador único del paciente.
     * @param desde      Fecha y hora de inicio del rango (inclusive).
     * @param hasta      Fecha y hora de fin del rango (inclusive).
     * @return Lista de {@link Cita} del paciente dentro del intervalo indicado.
     */
    List<Cita> findByPaciente_IdPacienteAndStartDatetimeBetween(
            Long idPaciente,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    /**
     * Recupera las citas de un psicólogo comprendidas dentro de un rango de fechas,
     * sin restricción de estado. Usada para obtener la agenda completa del psicólogo.
     *
     * @param idPsicologo Identificador único del psicólogo.
     * @param desde       Fecha y hora de inicio del rango (inclusive).
     * @param hasta       Fecha y hora de fin del rango (inclusive).
     * @return Lista de {@link Cita} del psicólogo dentro del intervalo indicado.
     */
    List<Cita> findByPsicologo_IdPsicologoAndStartDatetimeBetween(
            Long idPsicologo,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    /**
     * Recupera las citas de un psicólogo dentro de un rango de fechas, excluyendo
     * aquellas que se encuentren en un estado específico (por ejemplo, canceladas).
     * Permite mostrar la agenda operativa sin citas descartadas.
     *
     * @param idPsicologo Identificador único del psicólogo.
     * @param desde       Fecha y hora de inicio del rango (inclusive).
     * @param hasta       Fecha y hora de fin del rango (inclusive).
     * @param estado      Estado de cita que debe excluirse del resultado.
     * @return Lista de {@link Cita} del psicólogo en el rango indicado y con estado distinto al proporcionado.
     */
     List<Cita> findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
            Long idPsicologo,
            LocalDateTime desde,
            LocalDateTime hasta,
            EstadoCita estado
    );

    /**
     * Recupera las citas de un paciente dentro de un rango de fechas, ordenadas
     * cronológicamente por fecha de inicio ascendente. Útil para la vista de agenda del paciente.
     *
     * @param idPaciente Identificador único del paciente.
     * @param desde      Fecha y hora de inicio del rango (inclusive).
     * @param hasta      Fecha y hora de fin del rango (inclusive).
     * @return Lista de {@link Cita} del paciente en el rango indicado, ordenadas de más antigua a más reciente.
     */
    List<Cita> findByPaciente_IdPacienteAndStartDatetimeBetweenOrderByStartDatetimeAsc(
            Long idPaciente,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    /**
     * Recupera las citas cuya fecha de inicio se encuentra dentro de un rango y que además
     * tienen un estado concreto. Se emplea, por ejemplo, para localizar citas confirmadas
     * próximas a realizarse y así enviar recordatorios automáticos.
     *
     * @param desde  Fecha y hora de inicio del rango (inclusive).
     * @param hasta  Fecha y hora de fin del rango (inclusive).
     * @param name   Estado requerido para las citas ({@link EstadoCita}).
     * @return Lista de {@link Cita} que cumplen ambos criterios.
     */
    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, EstadoCita name);

    /**
     * Obtiene el identificador del usuario asociado al psicólogo de una cita concreta.
     *
     * <p>La consulta JPQL navega desde la cita hasta el usuario del psicólogo mediante
     * dos joins explícitos ({@code Cita → Psicologo → Usuario}). Se necesita esta consulta
     * personalizada porque el acceso directo desde la cita al usuario del psicólogo requiere
     * atravesar dos niveles de asociación.</p>
     *
     * @param idCita Identificador único de la cita.
     * @return Identificador del usuario ({@code idUsuario}) que corresponde al psicólogo de la cita.
     */
    @Query("""
            SELECT u.idUsuario
            FROM Cita c
            JOIN c.psicologo p
            JOIN p.usuario u
            WHERE c.idCita = :idCita
            """)
    Long obtenerUsuarioPsicologo(Long idCita);


    /**
     * Obtiene el identificador del usuario asociado al paciente de una cita concreta.
     *
     * <p>La consulta JPQL navega desde la cita hasta el usuario del paciente mediante
     * dos joins explícitos ({@code Cita → Paciente → Usuario}). Permite, por ejemplo,
     * enviar notificaciones Firebase al paciente sin cargar toda la entidad.</p>
     *
     * @param idCita Identificador único de la cita.
     * @return Identificador del usuario ({@code idUsuario}) que corresponde al paciente de la cita.
     */
    @Query("""
            SELECT u.idUsuario
            FROM Cita c
            JOIN c.paciente pa
            JOIN pa.usuario u
            WHERE c.idCita = :idCita
            """)
    Long obtenerUsuarioPaciente(Long idCita);


    /**
     * Recupera el historial completo de citas con todas sus relaciones cargadas en una única
     * consulta JPQL, ordenado por fecha de inicio descendente (las más recientes primero).
     *
     * <p>Utiliza {@code JOIN FETCH} para evitar el problema N+1 al cargar paciente, usuario
     * del paciente, psicólogo, usuario del psicólogo, tipo de terapia y pago asociado.
     * Destinado exclusivamente al panel de administración, donde se requiere el historial global.</p>
     *
     * @return Lista de {@link Cita} con todas sus relaciones inicializadas, ordenadas de más
     *         reciente a más antigua.
     */
    @Query("""
    SELECT c
    FROM Cita c
    JOIN FETCH c.paciente pa
    JOIN FETCH pa.usuario up
    JOIN FETCH c.psicologo ps
    JOIN FETCH ps.usuario us
    JOIN FETCH c.tipoTerapia tt
    LEFT JOIN FETCH c.pago p
    ORDER BY c.startDatetime DESC
""")
    List<Cita> obtenerHistorialCitas();
}
