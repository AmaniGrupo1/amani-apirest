package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaPacienteViewResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.events.CitaCanceladaEvent;
import com.amani.amaniapirest.events.CitaCreadaEvent;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio central de gestion de citas.
 *
 * <p>Agrupa la logica de negocio para tres perfiles de usuario:
 * <ul>
 *   <li><b>General (paciente)</b> — CRUD completo de citas.</li>
 *   <li><b>Administrador</b>     — CRUD con respuesta enriquecida ({@link CitaAdminResponseDTO}).</li>
 *   <li><b>Psicologo</b>         — Consulta y cambio de estado de citas propias.</li>
 * </ul>
 *
 * <p>Publica eventos de dominio ({@link CitaCreadaEvent}, {@link CitaCanceladaEvent})
 * para desacoplar las notificaciones del flujo principal.</p>
 */
@Service("citaServiceGeneral")
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoRepository psicologoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CitaService(CitaRepository citaRepository,
                       PacientesRepository pacientesRepository,
                       PsicologoRepository psicologoRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.citaRepository = citaRepository;
        this.pacientesRepository = pacientesRepository;
        this.psicologoRepository = psicologoRepository;
        this.eventPublisher = eventPublisher;
    }

    // =========================================================
    // MÉTODOS GENERALES
    // =========================================================

    /**
     * Obtiene todas las citas registradas en el sistema.
     *
     * @return lista de {@link CitaPacienteViewResponseDTO} con todas las citas
     */
    public List<CitaPacienteViewResponseDTO> findAll() {
        return citaRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca una cita por su identificador.
     *
     * @param idCita identificador de la cita
     * @return DTO con la informacion de la cita
     * @throws RuntimeException si la cita no existe
     */
    public CitaPacienteViewResponseDTO findById(Long idCita) {
        return toResponse(getCitaOrThrow(idCita));
    }

    /**
     * Crea una nueva cita y publica un {@link CitaCreadaEvent}.
     *
     * @param request datos de la cita a crear
     * @return DTO con la cita recien creada
     * @throws RuntimeException si el paciente o el psicologo no existen
     */
    @Transactional
    public CitaPacienteViewResponseDTO create(CitaRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        cita.setEstado(request.getEstado() != null ? request.getEstado() : EstadoCita.pendiente);
        cita.setMotivo(request.getMotivo());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        eventPublisher.publishEvent(new CitaCreadaEvent(this, saved));
        return toResponse(saved);
    }

    /**
     * Actualiza una cita existente. Si el nuevo estado es {@code cancelada},
     * publica un {@link CitaCanceladaEvent}.
     *
     * @param idCita  identificador de la cita a actualizar
     * @param request datos actualizados de la cita
     * @return DTO con la cita actualizada
     * @throws RuntimeException si la cita, el paciente o el psicologo no existen
     */
    @Transactional
    public CitaPacienteViewResponseDTO update(Long idCita, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(idCita);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : cita.getDurationMinutes());
        EstadoCita nuevoEstado = request.getEstado() != null ? request.getEstado() : cita.getEstado();
        cita.setEstado(nuevoEstado);
        cita.setMotivo(request.getMotivo());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        if (EstadoCita.cancelada.equals(nuevoEstado)) {
            eventPublisher.publishEvent(new CitaCanceladaEvent(this, saved, null));
        }
        return toResponse(saved);
    }

    /**
     * Elimina una cita por su identificador.
     *
     * @param idCita identificador de la cita a eliminar
     * @throws RuntimeException si la cita no existe
     */
    public void delete(Long idCita) {
        Cita cita = getCitaOrThrow(idCita);
        citaRepository.delete(cita);
    }

    /**
     * Obtiene una cita o lanza excepcion si no existe.
     *
     * @param idCita identificador de la cita
     * @return la entidad {@link Cita} encontrada
     * @throws RuntimeException si no se encuentra la cita
     */
    private Cita getCitaOrThrow(Long idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + idCita));
    }

    /**
     * Obtiene un paciente o lanza excepcion si no existe.
     *
     * @param idPaciente identificador del paciente
     * @return la entidad {@link Paciente} encontrada
     * @throws RuntimeException si no se encuentra el paciente
     */
    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    /**
     * Obtiene un psicologo o lanza excepcion si no existe.
     *
     * @param idPsicologo identificador del psicologo
     * @return la entidad {@link Psicologo} encontrada
     * @throws RuntimeException si no se encuentra el psicologo
     */
    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }

    /**
     * Convierte un texto a {@link EstadoCita}. Si es nulo o vacio, devuelve {@code pendiente}.
     *
     * @param estado texto con el nombre del estado
     * @return el enum {@link EstadoCita} correspondiente
     * @throws RuntimeException si el texto no corresponde a ningun estado valido
     */
    private EstadoCita parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return EstadoCita.pendiente;
        }
        try {
            return EstadoCita.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Estado de cita inválido: " + estado);
        }
    }

    /**
     * Convierte una entidad {@link Cita} en {@link CitaPacienteViewResponseDTO} (vista paciente).
     *
     * @param cita entidad a mapear
     * @return DTO de respuesta para el perfil paciente
     */
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

    // =========================================================
    // MÉTODOS ADMIN
    // =========================================================

    /**
     * Obtiene todas las citas con informacion enriquecida para el administrador.
     *
     * @return lista de {@link CitaAdminResponseDTO}
     */
    public List<CitaAdminResponseDTO> findAllAdmin() {
        return citaRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    /**
     * Busca una cita por identificador y devuelve la respuesta de administrador.
     *
     * @param idCita identificador de la cita
     * @return DTO enriquecido con datos de paciente y psicologo
     * @throws RuntimeException si la cita no existe
     */
    public CitaAdminResponseDTO findByIdAdmin(Long idCita) {
        return toAdminResponse(getCitaOrThrow(idCita));
    }

    /**
     * Crea una nueva cita desde el panel de administracion y publica un {@link CitaCreadaEvent}.
     *
     * @param request datos de la cita a crear
     * @return DTO de administrador con la cita recien creada
     * @throws RuntimeException si el paciente o el psicologo no existen
     */
    @Transactional
    public CitaAdminResponseDTO createAdmin(CitaRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        cita.setEstado(request.getEstado() != null ? request.getEstado() : EstadoCita.pendiente);
        cita.setMotivo(request.getMotivo());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        eventPublisher.publishEvent(new CitaCreadaEvent(this, saved));
        return toAdminResponse(saved);
    }

    /**
     * Actualiza una cita desde el panel de administracion. Si el nuevo estado es
     * {@code cancelada}, publica un {@link CitaCanceladaEvent} con origen "administrador".
     *
     * @param idCita  identificador de la cita a actualizar
     * @param request datos actualizados de la cita
     * @return DTO de administrador con la cita actualizada
     * @throws RuntimeException si la cita, el paciente o el psicologo no existen
     */
    @Transactional
    public CitaAdminResponseDTO updateAdmin(Long idCita, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(idCita);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : cita.getDurationMinutes());
        EstadoCita nuevoEstado = request.getEstado() != null ? request.getEstado() : cita.getEstado();
        cita.setEstado(nuevoEstado);
        cita.setMotivo(request.getMotivo());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita savedAdmin = citaRepository.save(cita);
        if (EstadoCita.cancelada.equals(nuevoEstado)) {
            eventPublisher.publishEvent(new CitaCanceladaEvent(this, savedAdmin, "administrador"));
        }
        return toAdminResponse(savedAdmin);
    }

    // =========================================================
    // MÉTODOS PSICÓLOGO
    // =========================================================

    /**
     * Obtiene todas las citas asignadas a un psicologo.
     *
     * @param idPsicologo identificador del psicologo
     * @return lista de {@link CitaPsicologoResponseDTO}
     */
    public List<CitaPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {
        return citaRepository.findByPsicologo_IdPsicologo(idPsicologo)
                .stream().map(this::toPsicologoResponse).toList();
    }

    /**
     * Busca una cita por identificador y devuelve la respuesta para el psicologo.
     *
     * @param idCita identificador de la cita
     * @return DTO con la informacion relevante para el psicologo
     * @throws RuntimeException si la cita no existe
     */
    public CitaPsicologoResponseDTO findByIdPsicologo(Long idCita) {
        return toPsicologoResponse(getCitaOrThrow(idCita));
    }

    /**
     * Permite al psicologo cambiar el estado de una cita. Si el nuevo estado es
     * {@code cancelada}, publica un {@link CitaCanceladaEvent} con origen "psicologo".
     *
     * @param idCita  identificador de la cita
     * @param request DTO que contiene el nuevo estado
     * @return DTO con la cita actualizada
     * @throws RuntimeException si la cita no existe
     */
    @Transactional
    public CitaPsicologoResponseDTO updateEstadoCita(Long idCita, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(idCita);
        EstadoCita nuevoEstadoPsi = request.getEstado() != null ? request.getEstado() : cita.getEstado();
        cita.setEstado(nuevoEstadoPsi);
        cita.setUpdatedAt(LocalDateTime.now());

        Cita savedPsi = citaRepository.save(cita);
        if (EstadoCita.cancelada.equals(nuevoEstadoPsi)) {
            eventPublisher.publishEvent(new CitaCanceladaEvent(this, savedPsi, "psicólogo"));
        }
        return toPsicologoResponse(savedPsi);
    }

    // =========================================================
    // MÉTODOS DE MAPEADO
    // =========================================================

    /**
     * Convierte una entidad {@link Cita} en {@link CitaAdminResponseDTO}.
     *
     * @param cita entidad a mapear
     * @return DTO de respuesta para el perfil administrador
     */
    private CitaAdminResponseDTO toAdminResponse(Cita cita) {
        Paciente p = cita.getPaciente();
        Psicologo ps = cita.getPsicologo();
        return new CitaAdminResponseDTO(
                p != null && p.getUsuario() != null ? p.getUsuario().getNombre() : null,
                p != null && p.getUsuario() != null ? p.getUsuario().getApellido() : null,
                ps != null ? ps.getIdPsicologo() : null,
                ps != null && ps.getUsuario() != null ? ps.getUsuario().getNombre() : null,
                ps != null && ps.getUsuario() != null ? ps.getUsuario().getApellido() : null,
                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado().name(),
                cita.getMotivo(),
                cita.getCreatedAt(),
                cita.getUpdatedAt()
        );
    }

    /**
     * Convierte una entidad {@link Cita} en {@link CitaPsicologoResponseDTO}.
     *
     * @param cita entidad a mapear
     * @return DTO de respuesta para el perfil psicologo
     */
    private CitaPsicologoResponseDTO toPsicologoResponse(Cita cita) {
        Paciente p = cita.getPaciente();
        return new CitaPsicologoResponseDTO(
                cita.getIdCita(),
                p != null ? p.getIdPaciente() : null,
                p != null && p.getUsuario() != null ? p.getUsuario().getNombre() : null,
                p != null && p.getUsuario() != null ? p.getUsuario().getApellido() : null,
                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado().name(),
                cita.getMotivo()
        );
    }
}