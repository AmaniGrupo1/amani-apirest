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
 * <p>Permite al panel de administración listar, consultar, crear y actualizar citas
 * entre pacientes y psicólogos, sin restricción de propietario. Cada operación
 * valida la existencia de los recursos referenciados antes de persistir.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
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

    /**
     * Obtiene todas las citas del sistema con datos completos de paciente y psicólogo.
     *
     * @return lista de {@link CitaAdminResponseDTO} con la información de todas las citas.
     */
    public List<CitaAdminResponseDTO> findAllAdmin() {
        return citaRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los datos completos de una cita identificada por su ID.
     *
     * @param id identificador único de la cita.
     * @return {@link CitaAdminResponseDTO} con la información de la cita.
     * @throws RuntimeException si no existe una cita con el identificador proporcionado.
     */
    public CitaAdminResponseDTO findByIdAdmin(Long id) {
        Cita cita = getCitaOrThrow(id);
        return toAdminResponse(cita);
    }

    /**
     * Crea una nueva cita asignando el paciente, psicólogo, fecha, duración, estado y motivo.
     *
     * @param request DTO con los datos necesarios para crear la cita.
     * @return {@link CitaAdminResponseDTO} con los datos de la cita recién creada.
     * @throws RuntimeException si el paciente o el psicólogo referenciado no existe.
     */
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

    /**
     * Actualiza los datos de una cita existente identificada por su ID.
     *
     * <p>Reemplaza el paciente, el psicólogo, la fecha, la duración, el estado y el motivo
     * con los valores del DTO proporcionado. Actualiza automáticamente la fecha de modificación.</p>
     *
     * @param id      identificador de la cita a actualizar.
     * @param request DTO con los nuevos valores de la cita.
     * @return {@link CitaAdminResponseDTO} con los datos actualizados.
     * @throws RuntimeException si la cita, el paciente o el psicólogo no existen.
     */
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

    /**
     * Convierte una entidad {@link Cita} en su representación DTO para el panel de administración.
     *
     * @param cita entidad a convertir.
     * @return {@link CitaAdminResponseDTO} con los datos del paciente, psicólogo y la cita.
     */
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

