package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AgendaPacienteItemDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaPacienteViewResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.TiposTerapia;
import com.amani.amaniapirest.repository.CitaRepository;
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
 * Servicio de negocio para gestionar citas entre pacientes y psicólogos.
 *
 * <p>Orquesta la creación, consulta, actualización y eliminación de citas,
 * validando la existencia de las entidades relacionadas ({@link Paciente} y
 * {@link Psicologo}) y resolviendo el estado de la cita a su valor enum
 * correspondiente.</p>
 */
@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoRepository psicologoRepository;
    private final TerapiaRepository terapiaRepository;


    /**
     * Obtiene la lista completa de citas registradas.
     *
     * @return lista de {@link CitaPacienteViewResponseDTO} con todas las citas
     */
    public List<CitaPacienteViewResponseDTO> findByPaciente(Long idPaciente) {
        return citaRepository
                .findByPaciente_IdPacienteOrderByStartDatetimeAsc(idPaciente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

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

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        cita.setEstado(request.getEstado());
        cita.setMotivo(request.getMotivo());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());
        TiposTerapia tipo = terapiaRepository.findById(request.getIdTipoTerapia())
                .orElseThrow(() -> new RuntimeException("Tipo de terapia no encontrado"));

        cita.setTipoTerapia(tipo);
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
    public CitaPacienteViewResponseDTO update(Long idCita, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(idCita);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : cita.getDurationMinutes());
        cita.setEstado(request.getEstado());
        cita.setMotivo(request.getMotivo());
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

        return CitaPacienteViewResponseDTO.builder()
                .idCita(cita.getIdCita())
                .fecha(start.toLocalDate())
                .horaInicio(start.toLocalTime())
                .horaFin(end.toLocalTime())
                .durationMinutes(cita.getDurationMinutes())
                .estado(cita.getEstado())
                .modalidad(cita.getModalidad())
                .motivo(cita.getMotivo())
                .tipoTerapia(
                        cita.getTipoTerapia() != null
                                ? cita.getTipoTerapia().getNombre()
                                : null
                )
                .minutosRestantes(minutosRestantes)
                .esProxima(minutosRestantes >= 0 && minutosRestantes <= 60)
                .build();
    }

    // Agenda mensual del paciente
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
