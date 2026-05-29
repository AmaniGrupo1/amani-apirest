package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AgendaPacienteItemDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaPacienteViewResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.*;
import com.amani.amaniapirest.repositories.BloqueoAgendaRepository;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.HorarioPsicologoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.terapiaService.TerapiaRepository;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestiona las citas del paciente autenticado desde su perspectiva de uso.
 *
 * <p>Expone operaciones de consulta de citas propias (por identificador, por mes)
 * y la cancelación de citas pendientes. La resolución de la identidad del paciente
 * se realiza a partir del contexto de seguridad JWT mediante
 * {@link #obtenerIdPacienteDesdeAuth}.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoRepository psicologoRepository;
    private final TerapiaRepository terapiaRepository;
    private final HorarioPsicologoRepository horarioRepository;
    private final BloqueoAgendaRepository bloqueoRepository;

    /**
     * Obtiene las citas de un paciente ordenadas cronológicamente de más antigua a más reciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link CitaPacienteViewResponseDTO} con las citas del paciente
     */
    public List<CitaPacienteViewResponseDTO> findByPaciente(Long idPaciente) {
        return citaRepository
                .findByPaciente_IdPacienteOrderByStartDatetimeAsc(idPaciente)
                .stream()
                // 🔥 SOLO citas activas (NO canceladas, NO completadas)
                .filter(cita -> cita.getEstado() != EstadoCita.cancelada
                        && cita.getEstado() != EstadoCita.completada)
                .map(this::toResponse)
                .toList();
    }

    /**
     * Extrae el identificador del paciente vinculado al usuario autenticado mediante JWT.
     *
     * @param auth objeto de autenticación del contexto de seguridad
     * @return identificador del paciente asociado al email del usuario autenticado
     * @throws RuntimeException si no existe un paciente vinculado al usuario autenticado
     */
    public Long obtenerIdPacienteDesdeAuth(Authentication auth) {
        String email = auth.getName();

        return pacientesRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"))
                .getIdPaciente();
    }
    /**
     * Busca una cita por su identificador único.
     *
     * @param idCita identificador de la cita a buscar
     * @return {@link CitaPacienteViewResponseDTO} con los datos de la cita encontrada
     * @throws RuntimeException si no existe una cita con el id proporcionado
     */
    public CitaPacienteViewResponseDTO findById(Long idCita) {
        return toResponse(getCitaOrThrow(idCita));
    }

    /**
     * Crea una nueva cita a partir de los datos del request.
     *
     * @param request {@link CitaRequestDTO} con los datos de la cita a crear
     * @return {@link CitaPacienteViewResponseDTO} con los datos de la cita creada
     * @throws RuntimeException si el paciente o el psicólogo referenciados no existen,
     *                          o si el estado proporcionado no es válido
     */
    public CitaPacienteViewResponseDTO create(CitaRequestDTO request) {

        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        if (request.getIdTipoTerapia() == null) {
            throw new RuntimeException("Debe seleccionar un tipo de terapia");
        }

        TiposTerapia tipo = terapiaRepository.findById(
                request.getIdTipoTerapia()
        ).orElseThrow(() ->
                new RuntimeException("Tipo de terapia no encontrado")
        );

        if (request.getStartDatetime() == null) {
            throw new RuntimeException("La fecha de la cita es obligatoria");
        }

        int duracion = request.getDurationMinutes() != null
                ? request.getDurationMinutes()
                : tipo.getDuracionMinutos();

        if (duracion <= 0) {
            throw new RuntimeException("Duración inválida");
        }

        LocalDateTime start = request.getStartDatetime();

        LocalDateTime end = start.plusMinutes(duracion);

        validarHorario(
                psicologo.getIdPsicologo(),
                start,
                end
        );

        validarBloqueos(
                psicologo.getIdPsicologo(),
                start,
                end
        );

        validarSolapamiento(
                psicologo.getIdPsicologo(),
                start,
                end,
                null
        );

        Cita cita = new Cita();

        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);

        cita.setTipoTerapia(tipo);

        cita.setStartDatetime(start);

        cita.setDurationMinutes(duracion);

        cita.setEstado(
                request.getEstado() != null
                        ? request.getEstado()
                        : EstadoCita.pendiente
        );

        cita.setMotivo(request.getMotivo());

        cita.setModalidad(request.getModalidad());

        cita.setCreatedAt(LocalDateTime.now());

        cita.setUpdatedAt(LocalDateTime.now());

        return toResponse(citaRepository.save(cita));
    }


    /**
     * Actualiza los datos de una cita existente.
     *
     * @param idCita  identificador de la cita a actualizar
     * @param request {@link CitaRequestDTO} con los nuevos datos de la cita
     * @return {@link CitaPacienteViewResponseDTO} con los datos actualizados
     * @throws RuntimeException si la cita, el paciente o el psicólogo no existen,
     *                          o si el estado proporcionado no es válido
     */
    public CitaPacienteViewResponseDTO update(
            Long idCita,
            CitaRequestDTO request
    ) {

        Cita cita = getCitaOrThrow(idCita);

        if (cita.getEstado() == EstadoCita.cancelada) {
            throw new RuntimeException(
                    "No se puede editar una cita cancelada"
            );
        }

        if (cita.getEstado() == EstadoCita.completada) {
            throw new RuntimeException(
                    "No se puede editar una cita completada"
            );
        }

        Paciente paciente = getPacienteOrThrow(
                request.getIdPaciente()
        );

        Psicologo psicologo = getPsicologoOrThrow(
                request.getIdPsicologo()
        );

        LocalDateTime start = request.getStartDatetime();

        int duracion = request.getDurationMinutes() != null
                ? request.getDurationMinutes()
                : cita.getDurationMinutes();

        LocalDateTime end = start.plusMinutes(duracion);

        validarHorario(
                psicologo.getIdPsicologo(),
                start,
                end
        );

        validarBloqueos(
                psicologo.getIdPsicologo(),
                start,
                end
        );

        validarSolapamiento(
                psicologo.getIdPsicologo(),
                start,
                end,
                cita.getIdCita()
        );

        cita.setPaciente(paciente);

        cita.setPsicologo(psicologo);

        cita.setStartDatetime(start);

        cita.setDurationMinutes(duracion);

        cita.setMotivo(request.getMotivo());

        if (request.getEstado() != null) {
            cita.setEstado(request.getEstado());
        }

        cita.setUpdatedAt(LocalDateTime.now());

        return toResponse(citaRepository.save(cita));
    }

    /**
     * Elimina la cita con el identificador indicado.
     *
     * @param idCita identificador de la cita a eliminar
     * @throws RuntimeException si no existe una cita con el id proporcionado
     */
    public void delete(Long idCita) {
        Cita cita = getCitaOrThrow(idCita);
        citaRepository.delete(cita);
    }

    /**
     * Recupera una cita por id o lanza excepción si no existe.
     *
     * @param idCita identificador de la cita
     * @return entidad {@link Cita} encontrada
     * @throws RuntimeException si no existe una cita con el id proporcionado
     */
    private Cita getCitaOrThrow(Long idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + idCita));
    }

    /**
     * Recupera un paciente por id o lanza excepción si no existe.
     *
     * @param idPaciente identificador del paciente
     * @return entidad {@link Paciente} encontrada
     * @throws RuntimeException si no existe un paciente con el id proporcionado
     */
    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private void validarHorario(
            Long idPsicologo,
            LocalDateTime start,
            LocalDateTime end
    ) {

        short diaSemana = (short) (start.getDayOfWeek().getValue() - 1);

        boolean dentroHorario = horarioRepository
                .findByPsicologoIdPsicologoAndActivoTrue(idPsicologo)
                .stream()
                .filter(h -> h.getDiaSemana() == diaSemana)
                .anyMatch(h ->
                        !start.toLocalTime().isBefore(h.getHoraInicio()) &&
                                !end.toLocalTime().isAfter(h.getHoraFin())
                );

        if (!dentroHorario) {
            throw new RuntimeException("Fuera del horario del psicólogo");
        }
    }

    private void validarBloqueos(
            Long idPsicologo,
            LocalDateTime start,
            LocalDateTime end
    ) {

        boolean bloqueado = bloqueoRepository
                .findByPsicologoIdPsicologoAndFecha(
                        idPsicologo,
                        start.toLocalDate()
                )
                .stream()
                .anyMatch(b -> {

                    // día completo
                    if (b.getHoraInicio() == null || b.getHoraFin() == null) {
                        return true;
                    }

                    // bloqueo parcial
                    return start.toLocalTime().isBefore(b.getHoraFin()) &&
                            end.toLocalTime().isAfter(b.getHoraInicio());
                });

        if (bloqueado) {
            throw new RuntimeException("Horario bloqueado");
        }
    }

    private void validarSolapamiento(
            Long idPsicologo,
            LocalDateTime start,
            LocalDateTime end,
            Long idExcluir
    ) {

        LocalDateTime inicioDia = start.toLocalDate().atStartOfDay();
        LocalDateTime finDia = start.toLocalDate()
                .plusDays(1)
                .atStartOfDay();

        boolean conflicto = citaRepository
                .findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
                        idPsicologo,
                        inicioDia,
                        finDia,
                        EstadoCita.cancelada
                )
                .stream()
                .filter(c ->
                        idExcluir == null ||
                                !c.getIdCita().equals(idExcluir)
                )
                .anyMatch(c -> {

                    LocalDateTime existingStart = c.getStartDatetime();

                    LocalDateTime existingEnd =
                            existingStart.plusMinutes(
                                    c.getDurationMinutes()
                            );

                    return existingStart.isBefore(end) &&
                            existingEnd.isAfter(start);
                });

        if (conflicto) {
            throw new RuntimeException("Ya existe una cita en ese horario");
        }
    }


    /**
     * Recupera un psicólogo por id o lanza excepción si no existe.
     *
     * @param idPsicologo identificador del psicólogo
     * @return entidad {@link Psicologo} encontrada
     * @throws RuntimeException si no existe un psicólogo con el id proporcionado
     */
    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }


    private EstadoCita parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return EstadoCita.pendiente;
        }
        try {
            return EstadoCita.valueOf(estado.toLowerCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Estado de cita inválido: " + estado);
        }
    }


    private CitaPacienteViewResponseDTO toResponse(Cita cita) {

        LocalDateTime start = cita.getStartDatetime();
        LocalDateTime end = start.plusMinutes(cita.getDurationMinutes());

        long minutosRestantes = Duration
                .between(LocalDateTime.now(), start)
                .toMinutes();

        Pago pago = cita.getPago();

        return CitaPacienteViewResponseDTO.builder()
                .idCita(cita.getIdCita())

                // IDS
                .idPaciente(cita.getPaciente().getIdPaciente())
                .idPsicologo(cita.getPsicologo().getIdPsicologo())
                .idTipoTerapia(
                        cita.getTipoTerapia() != null
                                ? cita.getTipoTerapia().getIdTipo()
                                : null
                )

                // FECHAS
                .fecha(start.toLocalDate())
                .horaInicio(start.toLocalTime())
                .horaFin(end.toLocalTime())

                // DURACIÓN
                .durationMinutes(cita.getDurationMinutes())

                // ENUMS
                .estado(cita.getEstado())
                .modalidad(cita.getModalidad())

                // INFO
                .motivo(cita.getMotivo())

                .tipoTerapia(
                        cita.getTipoTerapia() != null
                                ? cita.getTipoTerapia().getNombre()
                                : null
                )

                // TIEMPO RESTANTE
                .minutosRestantes(minutosRestantes)

                .esProxima(
                        minutosRestantes >= 0 &&
                                minutosRestantes <= 60
                )

                // PAGO
                .metodoPago(
                        pago != null
                                ? pago.getMetodoPago()
                                : null
                )

                .estadoPago(
                        pago != null
                                ? pago.getEstadoPago()
                                : null
                )

                .build();
    }

    /**
     * Devuelve la agenda mensual del paciente con las citas comprendidas en el mes indicado.
     *
     * @param idPaciente identificador del paciente
     * @param month      mes en formato {@code YYYY-MM} (por ejemplo, {@code "2025-06"})
     * @return lista de {@link AgendaPacienteItemDTO} ordenada cronológicamente
     */
    public List<AgendaPacienteItemDTO> getAgendaPacienteMes(Long idPaciente, String month) {

        YearMonth yearMonth = YearMonth.parse(month);

        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<Cita> citas = citaRepository
                .findByPaciente_IdPacienteAndStartDatetimeBetweenOrderByStartDatetimeAsc(
                        idPaciente, start, end
                );

        return citas.stream()
                .map(cita -> new AgendaPacienteItemDTO(
                        cita.getStartDatetime().toLocalDate(),
                        cita.getStartDatetime().toLocalTime(),
                        cita.getStartDatetime().toLocalTime()
                                .plusMinutes(cita.getDurationMinutes()),
                        cita.getEstado() != null ? cita.getEstado().name() : null,
                        cita.getMotivo(),
                        cita.getIdCita()
                ))
                .toList();
    }
}
