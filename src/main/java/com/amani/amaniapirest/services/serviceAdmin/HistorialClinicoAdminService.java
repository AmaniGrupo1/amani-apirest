package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.response.HistorialClinicoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.models.HistorialClinico;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.hostorialClinico.HistorialClinicoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de administración para gestionar el historial clínico de todos los pacientes.
 *
 * <p>Proporciona operaciones CRUD sobre los registros de historial clínico desde el
 * panel de administración, sin restricción de propietario. Cada operación de escritura
 * valida la existencia del paciente referenciado antes de persistir.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Service
public class HistorialClinicoAdminService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final PacientesRepository pacientesRepository;

    public HistorialClinicoAdminService(HistorialClinicoRepository historialClinicoRepository,
                                        PacientesRepository pacientesRepository) {
        this.historialClinicoRepository = historialClinicoRepository;
        this.pacientesRepository = pacientesRepository;
    }

    // ============================
    // MÉTODOS CRUD ADMIN
    // ============================

    /**
     * Obtiene todos los registros de historial clínico del sistema.
     *
     * @return lista de {@link HistorialClinicoAdminResponseDTO} con el detalle de cada registro.
     */
    public List<HistorialClinicoAdminResponseDTO> findAllAdmin() {
        return historialClinicoRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un registro de historial clínico por su identificador.
     *
     * @param idHistory identificador único del historial clínico.
     * @return {@link HistorialClinicoAdminResponseDTO} con los datos del registro.
     * @throws RuntimeException si no existe un historial con el identificador proporcionado.
     */
    public HistorialClinicoAdminResponseDTO findByIdAdmin(Long idHistory) {
        HistorialClinico historial = getHistorialOrThrow(idHistory);
        return toAdminResponse(historial);
    }

    /**
     * Crea un nuevo registro de historial clínico para el paciente indicado.
     *
     * @param request DTO con el ID del paciente, título, diagnóstico, tratamiento y observaciones.
     * @return {@link HistorialClinicoAdminResponseDTO} con los datos del registro creado.
     * @throws RuntimeException si el paciente referenciado no existe.
     */
    public HistorialClinicoAdminResponseDTO createAdmin(HistorialClinicoRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());
        historial.setCreadoEn(LocalDateTime.now());

        return toAdminResponse(historialClinicoRepository.save(historial));
    }

    /**
     * Actualiza un registro de historial clínico existente.
     *
     * @param idHistory identificador del registro a actualizar.
     * @param request   DTO con los nuevos valores del historial.
     * @return {@link HistorialClinicoAdminResponseDTO} con los datos actualizados.
     * @throws RuntimeException si el historial o el paciente referenciado no existen.
     */
    public HistorialClinicoAdminResponseDTO updateAdmin(Long idHistory, HistorialClinicoRequestDTO request) {
        HistorialClinico historial = getHistorialOrThrow(idHistory);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toAdminResponse(historialClinicoRepository.save(historial));
    }

    /**
     * Elimina un registro de historial clínico por su identificador.
     *
     * @param idHistory identificador del registro a eliminar.
     * @throws RuntimeException si no existe un historial con el identificador proporcionado.
     */
    public void deleteAdmin(Long idHistory) {
        historialClinicoRepository.delete(getHistorialOrThrow(idHistory));
    }

    // ============================
    // MÉTODOS AUXILIARES
    // ============================

    private HistorialClinico getHistorialOrThrow(Long idHistory) {
        return historialClinicoRepository.findById(idHistory)
                .orElseThrow(() -> new RuntimeException("Historial clínico no encontrado con id: " + idHistory));
    }

    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private HistorialClinicoAdminResponseDTO toAdminResponse(HistorialClinico historial) {
        Paciente paciente = historial.getPaciente();
        return new HistorialClinicoAdminResponseDTO(
                paciente.getUsuario().getNombre(),
                paciente.getUsuario().getApellido(),
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }
}