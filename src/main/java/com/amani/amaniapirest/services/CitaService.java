package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.CitaPsicologoResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaResponseDTO;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar citas entre pacientes y psicólogos.
 *
 * <p>Orquesta la creación, consulta, actualización y eliminación de citas
 * con métodos específicos por rol: administrador, psicólogo y paciente.
 * Cada rol accede únicamente a los datos que le corresponden y recibe
 * el DTO apropiado según su nivel de privilegio.</p>
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

    /**
     * Obtiene la lista completa de citas registradas.
     *
     * @return lista de {@link CitaResponseDTO} con todas las citas
     */
    public List<CitaResponseDTO> findAll() {
        return citaRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca una cita por su identificador único.
     *
     * @param idCita identificador de la cita a buscar
     * @return {@link CitaResponseDTO} con los datos de la cita encontrada
     * @throws RuntimeException si no existe una cita con el id proporcionado
     */
    public CitaResponseDTO findById(Long idCita) {
        return toResponse(getCitaOrThrow(idCita));
    }

    /**
     * Crea una nueva cita a partir de los datos del request.
     *
     * @param request {@link CitaRequestDTO} con los datos de la cita a crear
     * @return {@link CitaResponseDTO} con los datos de la cita creada
     * @throws RuntimeException si el paciente o el psicólogo referenciados no existen,
     *                          o si el estado proporcionado no es válido
     */
    @Transactional
    public CitaResponseDTO create(CitaRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        cita.setEstado(parseEstado(request.getEstado()));
        cita.setMotivo(request.getMotivo());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        eventPublisher.publishEvent(new CitaCreadaEvent(this, saved));
        return toResponse(saved);
    }

    /**
     * Actualiza los datos de una cita existente.
     *
     * @param idCita  identificador de la cita a actualizar
     * @param request {@link CitaRequestDTO} con los nuevos datos de la cita
     * @return {@link CitaResponseDTO} con los datos actualizados
     * @throws RuntimeException si la cita, el paciente o el psicólogo no existen,
     *                          o si el estado proporcionado no es válido
     */
    @Transactional
    public CitaResponseDTO update(Long idCita, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(idCita);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : cita.getDurationMinutes());
        String nuevoEstado = request.getEstado() != null ? parseEstado(request.getEstado()) : cita.getEstado();
        cita.setEstado(nuevoEstado);
        cita.setMotivo(request.getMotivo());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        if (EstadoCita.cancelada.name().equals(nuevoEstado)) {
            eventPublisher.publishEvent(new CitaCanceladaEvent(this, saved, null));
        }
        return toResponse(saved);
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

    /**
     * Convierte un texto de estado al enum {@link EstadoCita} correspondiente.
     * Si el valor es nulo o vacío, devuelve {@link EstadoCita#pendiente} como valor por defecto.
     *
     * @param estado nombre del estado en texto (insensible a mayúsculas)
     * @return constante {@link EstadoCita} correspondiente
     * @throws RuntimeException si el valor no corresponde a ningún estado válido
     */
    private String parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return EstadoCita.pendiente.name();
        }
        try {
            return EstadoCita.valueOf(estado.toLowerCase()).name();
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Estado de cita inválido: " + estado);
        }
    }

    /**
     * Convierte una entidad {@link Cita} en su DTO de respuesta.
     *
     * @param cita entidad a convertir
     * @return {@link CitaResponseDTO} con los datos mapeados
     */
    private CitaResponseDTO toResponse(Cita cita) {
        return new CitaResponseDTO(
                cita.getIdCita(),
                cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null,
                cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null,
                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado() != null ? cita.getEstado() : EstadoCita.pendiente.name(),
                cita.getMotivo()
        );
    }

    // =========================================================
    // MÉTODOS PARA ROL: ADMIN
    // =========================================================

    /**
     * Obtiene la lista completa de citas (vista de administrador).
     *
     * @return lista de {@link CitaAdminResponseDTO} con todas las citas y sus participantes
     */
    public List<CitaAdminResponseDTO> findAllAdmin() {
        return citaRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    /**
     * Busca una cita por su identificador (vista de administrador).
     *
     * @param idCita identificador de la cita
     * @return {@link CitaAdminResponseDTO} con los datos completos de la cita
     * @throws RuntimeException si no existe la cita
     */
    public CitaAdminResponseDTO findByIdAdmin(Long idCita) {
        return toAdminResponse(getCitaOrThrow(idCita));
    }

    /**
     * Crea una nueva cita desde la vista de administrador.
     *
     * @param request {@link CitaRequestDTO} con todos los datos de la cita
     * @return {@link CitaAdminResponseDTO} con los datos de la cita creada
     * @throws RuntimeException si el paciente o el psicólogo no existen
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
        cita.setEstado(request.getEstado() != null ? parseEstado(request.getEstado()) : EstadoCita.pendiente.name());
        cita.setMotivo(request.getMotivo());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        eventPublisher.publishEvent(new CitaCreadaEvent(this, saved));
        return toAdminResponse(saved);
    }

    /**
     * Actualiza una cita existente desde la vista de administrador.
     *
     * @param idCita  identificador de la cita a actualizar
     * @param request {@link CitaRequestDTO} con los nuevos datos
     * @return {@link CitaAdminResponseDTO} con los datos actualizados
     * @throws RuntimeException si la cita, el paciente o el psicólogo no existen
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
        String nuevoEstadoAdmin = request.getEstado() != null ? parseEstado(request.getEstado()) : cita.getEstado();
        cita.setEstado(nuevoEstadoAdmin);
        cita.setMotivo(request.getMotivo());
        cita.setUpdatedAt(LocalDateTime.now());

        Cita savedAdmin = citaRepository.save(cita);
        if (EstadoCita.cancelada.name().equals(nuevoEstadoAdmin)) {
            eventPublisher.publishEvent(new CitaCanceladaEvent(this, savedAdmin, "administrador"));
        }
        return toAdminResponse(savedAdmin);
    }

    // =========================================================
    // MÉTODOS PARA ROL: PSICÓLOGO
    // =========================================================

    /**
     * Obtiene todas las citas asignadas a un psicólogo concreto.
     *
     * @param idPsicologo identificador del psicólogo autenticado
     * @return lista de {@link CitaPsicologoResponseDTO} con las citas del psicólogo
     */
    public List<CitaPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {
        return citaRepository.findByPsicologo_IdPsicologo(idPsicologo)
                .stream().map(this::toPsicologoResponse).toList();
    }

    /**
     * Busca una cita por id desde la perspectiva del psicólogo.
     *
     * @param idCita identificador de la cita
     * @return {@link CitaPsicologoResponseDTO} con los datos visibles para el psicólogo
     * @throws RuntimeException si no existe la cita
     */
    public CitaPsicologoResponseDTO findByIdPsicologo(Long idCita) {
        return toPsicologoResponse(getCitaOrThrow(idCita));
    }

    /**
     * Permite al psicólogo actualizar el estado de una cita asignada a él.
     *
     * @param idCita  identificador de la cita a actualizar
     * @param request {@link CitaRequestDTO} con el nuevo estado
     * @return {@link CitaPsicologoResponseDTO} con los datos actualizados
     * @throws RuntimeException si no existe la cita
     */
    @Transactional
    public CitaPsicologoResponseDTO updateEstadoCita(Long idCita, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(idCita);
        String nuevoEstadoPsicologo = request.getEstado() != null ? parseEstado(request.getEstado()) : cita.getEstado();
        cita.setEstado(nuevoEstadoPsicologo);
        cita.setUpdatedAt(LocalDateTime.now());
        Cita savedPsi = citaRepository.save(cita);
        if (EstadoCita.cancelada.name().equals(nuevoEstadoPsicologo)) {
            eventPublisher.publishEvent(new CitaCanceladaEvent(this, savedPsi, "psicólogo"));
        }
        return toPsicologoResponse(savedPsi);
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE MAPEO
    // =========================================================

    /**
     * Convierte una entidad {@link Cita} en {@link CitaAdminResponseDTO}.
     *
     * @param cita entidad a convertir
     * @return DTO con la vista completa para administrador
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
                cita.getEstado() != null ? cita.getEstado() : EstadoCita.pendiente.name(),
                cita.getMotivo(),
                cita.getCreatedAt(),
                cita.getUpdatedAt()
        );
    }

    /**
     * Convierte una entidad {@link Cita} en {@link CitaPsicologoResponseDTO}.
     *
     * @param cita entidad a convertir
     * @return DTO con la vista filtrada para psicólogo
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
                cita.getEstado() != null ? cita.getEstado() : EstadoCita.pendiente.name(),
                cita.getMotivo()
        );
    }
}

