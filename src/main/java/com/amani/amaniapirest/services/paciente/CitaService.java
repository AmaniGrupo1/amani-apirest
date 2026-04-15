package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.AgendaPacienteItemDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.CitaResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import org.springframework.stereotype.Service;

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
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoRepository psicologoRepository;

    /**
     * Construye el servicio inyectando los repositorios necesarios.
     *
     * @param citaRepository      repositorio JPA de {@link Cita}
     * @param pacientesRepository repositorio JPA de {@link Paciente}
     * @param psicologoRepository repositorio JPA de {@link Psicologo}
     */
    public CitaService(CitaRepository citaRepository,
                       PacientesRepository pacientesRepository,
                       PsicologoRepository psicologoRepository) {
        this.citaRepository = citaRepository;
        this.pacientesRepository = pacientesRepository;
        this.psicologoRepository = psicologoRepository;
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
    public CitaResponseDTO create(CitaRequestDTO request) {
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

        return toResponse(citaRepository.save(cita));
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
    public CitaResponseDTO update(Long idCita, CitaRequestDTO request) {
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


    private CitaResponseDTO toResponse(Cita cita) {
        return new CitaResponseDTO(
                cita.getIdCita(),
                cita.getPaciente() != null ? cita.getPaciente().getUsuario().getPaciente().getUsuario().getIdUsuario() : null,
                cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null,
                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado(),
                cita.getMotivo()
        );
    }

    // Agenda mensual del paciente
    public List<AgendaPacienteItemDTO> getAgendaPacienteMes(Long idPaciente, String month) {
        // Parsear el mes recibido (YYYY-MM)
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        // Buscar citas del paciente en ese rango
        List<Cita> citas = citaRepository.findByPaciente_IdPaciente(idPaciente).stream()
                .filter(c -> !c.getStartDatetime().isBefore(start) && !c.getStartDatetime().isAfter(end))
                .collect(Collectors.toList());
        // Mapear a DTO
        return citas.stream().map(cita -> new AgendaPacienteItemDTO(
                cita.getStartDatetime().toLocalDate(),
                cita.getStartDatetime().toLocalTime(),
                cita.getStartDatetime().toLocalTime().plusMinutes(cita.getDurationMinutes()),
                cita.getEstado() != null ? cita.getEstado().name() : null,
                cita.getMotivo(),
                cita.getIdCita()
        )).collect(Collectors.toList());
    }
}
