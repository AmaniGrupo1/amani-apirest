package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.models.HistorialClinico;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.HistorialClinicoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar el historial clínico de los pacientes.
 *
 * <p>Permite crear, consultar, actualizar y eliminar registros clínicos,
 * validando la existencia del paciente referenciado en cada operación.</p>
 */
@Service
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final PacientesRepository pacientesRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param historialClinicoRepository repositorio JPA de {@link HistorialClinico}
     * @param pacientesRepository        repositorio JPA de {@link Paciente}
     */
    public HistorialClinicoService(HistorialClinicoRepository historialClinicoRepository,
                                   PacientesRepository pacientesRepository) {
        this.historialClinicoRepository = historialClinicoRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /**
     * Obtiene la lista completa de registros del historial clínico.
     *
     * @return lista de {@link HistorialClinicoResponseDTO} con todos los registros
     */
    public List<HistorialClinicoResponseDTO> findAll() {
        return historialClinicoRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un registro clínico por su identificador único.
     *
     * @param idHistory identificador del registro
     * @return {@link HistorialClinicoResponseDTO} con los datos encontrados
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    public HistorialClinicoResponseDTO findById(Long idHistory) {
        return toResponse(getHistorialOrThrow(idHistory));
    }

    /**
     * Obtiene todos los registros del historial clínico de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link HistorialClinicoResponseDTO} del paciente indicado
     */
    public List<HistorialClinicoResponseDTO> findByPaciente(Long idPaciente) {
        return historialClinicoRepository.findByPaciente_IdPaciente(idPaciente)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Crea un nuevo registro en el historial clínico de un paciente.
     *
     * @param request {@link HistorialClinicoRequestDTO} con los datos del registro
     * @return {@link HistorialClinicoResponseDTO} con los datos creados
     * @throws RuntimeException si el paciente referenciado no existe
     */
    public HistorialClinicoResponseDTO create(HistorialClinicoRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());
        historial.setCreadoEn(LocalDateTime.now());

        return toResponse(historialClinicoRepository.save(historial));
    }

    /**
     * Actualiza un registro clínico existente.
     *
     * @param idHistory identificador del registro a actualizar
     * @param request   {@link HistorialClinicoRequestDTO} con los nuevos datos
     * @return {@link HistorialClinicoResponseDTO} con los datos actualizados
     * @throws RuntimeException si el registro o el paciente referenciado no existen
     */
    public HistorialClinicoResponseDTO update(Long idHistory, HistorialClinicoRequestDTO request) {
        HistorialClinico historial = getHistorialOrThrow(idHistory);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toResponse(historialClinicoRepository.save(historial));
    }

    /**
     * Elimina el registro clínico con el identificador indicado.
     *
     * @param idHistory identificador del registro a eliminar
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    public void delete(Long idHistory) {
        historialClinicoRepository.delete(getHistorialOrThrow(idHistory));
    }

    /**
     * Recupera un registro clínico por id o lanza excepción si no existe.
     *
     * @param idHistory identificador del registro
     * @return entidad {@link HistorialClinico} encontrada
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    private HistorialClinico getHistorialOrThrow(Long idHistory) {
        return historialClinicoRepository.findById(idHistory)
                .orElseThrow(() -> new RuntimeException("Historial clínico no encontrado con id: " + idHistory));
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
     * Convierte una entidad {@link HistorialClinico} en su DTO de respuesta.
     *
     * @param historial entidad a convertir
     * @return {@link HistorialClinicoResponseDTO} con los datos mapeados
     */
    private HistorialClinicoResponseDTO toResponse(HistorialClinico historial) {
        return new HistorialClinicoResponseDTO(
                historial.getIdHistory(),
                historial.getPaciente() != null ? historial.getPaciente().getIdPaciente() : null,
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }
}

