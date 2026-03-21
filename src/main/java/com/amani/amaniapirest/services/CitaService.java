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

    public List<CitaResponseDTO> findAll() {
        return citaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CitaResponseDTO findById(Long idCita) {
        return toResponse(getCitaOrThrow(idCita));
    }

    @Transactional
    public CitaResponseDTO create(CitaRequestDTO request) {
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

    @Transactional
    public CitaResponseDTO update(Long idCita, CitaRequestDTO request) {
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

    public void delete(Long idCita) {
        Cita cita = getCitaOrThrow(idCita);
        citaRepository.delete(cita);
    }

    private Cita getCitaOrThrow(Long idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + idCita));
    }

    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }

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

    private CitaResponseDTO toResponse(Cita cita) {
        return new CitaResponseDTO(
                cita.getIdCita(),
                cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null,
                cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null,
                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado() != null ? cita.getEstado() : EstadoCita.pendiente,
                cita.getMotivo()
        );
    }

    // =========================================================
    // MÉTODOS ADMIN
    // =========================================================

    public List<CitaAdminResponseDTO> findAllAdmin() {
        return citaRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    public CitaAdminResponseDTO findByIdAdmin(Long idCita) {
        return toAdminResponse(getCitaOrThrow(idCita));
    }

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

    public List<CitaPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {
        return citaRepository.findByPsicologo_IdPsicologo(idPsicologo)
                .stream().map(this::toPsicologoResponse).toList();
    }

    public CitaPsicologoResponseDTO findByIdPsicologo(Long idCita) {
        return toPsicologoResponse(getCitaOrThrow(idCita));
    }

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