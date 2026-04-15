package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de administración para consultar y gestionar todas las citas del sistema.
 *
 * @see com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO
 */
@Service
public class CitaAdminService {

    private final CitaRepository citaRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoRepository psicologoRepository;

    public CitaAdminService(CitaRepository citaRepository,
                            PacientesRepository pacientesRepository,
                            PsicologoRepository psicologoRepository) {
        this.citaRepository = citaRepository;
        this.pacientesRepository = pacientesRepository;
        this.psicologoRepository = psicologoRepository;
    }

    public List<CitaAdminResponseDTO> findAllAdmin() {
        return citaRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    /** Obtiene una cita específica con datos completos (admin). */
    public CitaAdminResponseDTO findByIdAdmin(Long id) {
        Cita cita = getCitaOrThrow(id);
        return toAdminResponse(cita);
    }

    /** Crea una nueva cita desde la vista admin. */
    public CitaAdminResponseDTO createAdmin(CitaRequestDTO request) {
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

        return toAdminResponse(citaRepository.save(cita));
    }

    /** Actualiza una cita existente desde la vista admin. */
    public CitaAdminResponseDTO updateAdmin(Long id, CitaRequestDTO request) {
        Cita cita = getCitaOrThrow(id);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());
        Psicologo psicologo = getPsicologoOrThrow(request.getIdPsicologo());

        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(request.getStartDatetime());
        cita.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : cita.getDurationMinutes());
        cita.setEstado(request.getEstado());
        cita.setMotivo(request.getMotivo());
        cita.setUpdatedAt(LocalDateTime.now());

        return toAdminResponse(citaRepository.save(cita));
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private Cita getCitaOrThrow(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + id));
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
        if (estado == null || estado.isBlank()) return EstadoCita.pendiente;
        try {
            return EstadoCita.valueOf(estado.toLowerCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Estado de cita inválido: " + estado);
        }
    }

    /** Convierte una entidad Cita a CitaAdminResponseDTO con datos completos. */
    private CitaAdminResponseDTO toAdminResponse(Cita cita) {
        return new CitaAdminResponseDTO(
                cita.getPaciente().getUsuario().getNombre(),
                cita.getPaciente().getUsuario().getApellido(),
                cita.getPsicologo().getIdPsicologo(),
                cita.getPsicologo().getUsuario().getNombre(),
                cita.getPsicologo().getUsuario().getApellido(),

                cita.getStartDatetime(),
                cita.getDurationMinutes(),
                cita.getEstado().name(),
                cita.getMotivo(),

                cita.getCreatedAt(),
                cita.getUpdatedAt()
        );
    }
}

