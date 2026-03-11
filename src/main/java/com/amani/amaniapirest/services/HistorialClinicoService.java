package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoAdmin.request.HistorialClinicoAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.HistorialClinicoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.request.HistorialClinicoPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.HistorialClinicoPsicologoResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.models.HistorialClinico;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.HistorialClinicoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar el historial clínico de los pacientes.
 *
 * <p>Proporciona métodos específicos por rol: el administrador tiene acceso
 * total con información detallada del paciente; el psicólogo puede crear y
 * consultar registros de sus propios pacientes; el paciente solo puede
 * consultar su propio historial.</p>
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

    // =========================================================
    // MÉTODOS PARA ROL: ADMIN
    // =========================================================

    /**
     * Obtiene la lista completa del historial clínico (vista de administrador).
     *
     * @return lista de {@link HistorialClinicoAdminResponseDTO} con todos los registros
     */
    public List<HistorialClinicoAdminResponseDTO> findAllAdmin() {
        return historialClinicoRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    /**
     * Busca un registro clínico por su id (vista de administrador).
     *
     * @param idHistory identificador del registro
     * @return {@link HistorialClinicoAdminResponseDTO} con los datos completos
     * @throws RuntimeException si no existe el registro
     */
    public HistorialClinicoAdminResponseDTO findByIdAdmin(Long idHistory) {
        return toAdminResponse(getHistorialOrThrow(idHistory));
    }

    /**
     * Crea un registro clínico desde la vista de administrador.
     *
     * @param request {@link HistorialClinicoAdminRequestDTO} con los datos del registro
     * @return {@link HistorialClinicoAdminResponseDTO} con los datos del registro creado
     * @throws RuntimeException si el paciente no existe
     */
    public HistorialClinicoAdminResponseDTO createAdmin(HistorialClinicoAdminRequestDTO request) {
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
     * Actualiza un registro clínico desde la vista de administrador.
     *
     * @param idHistory identificador del registro a actualizar
     * @param request   {@link HistorialClinicoAdminRequestDTO} con los nuevos datos
     * @return {@link HistorialClinicoAdminResponseDTO} con los datos actualizados
     * @throws RuntimeException si el registro o el paciente no existen
     */
    public HistorialClinicoAdminResponseDTO updateAdmin(Long idHistory, HistorialClinicoAdminRequestDTO request) {
        HistorialClinico historial = getHistorialOrThrow(idHistory);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toAdminResponse(historialClinicoRepository.save(historial));
    }

    // =========================================================
    // MÉTODOS PARA ROL: PSICÓLOGO
    // =========================================================

    /**
     * Obtiene el historial clínico de un paciente desde la perspectiva del psicólogo.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link HistorialClinicoPsicologoResponseDTO} del paciente indicado
     */
    public List<HistorialClinicoPsicologoResponseDTO> findByPacientePsicologo(Long idPaciente) {
        return historialClinicoRepository.findByPaciente_IdPaciente(idPaciente)
                .stream().map(this::toPsicologoResponse).toList();
    }

    /**
     * Crea un registro clínico desde la perspectiva del psicólogo.
     *
     * @param request {@link HistorialClinicoPsicologoRequestDTO} con los datos del registro
     * @return {@link HistorialClinicoPsicologoResponseDTO} con los datos del registro creado
     * @throws RuntimeException si el paciente no existe
     */
    public HistorialClinicoPsicologoResponseDTO createPsicologo(HistorialClinicoPsicologoRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());
        historial.setCreadoEn(LocalDateTime.now());

        return toPsicologoResponse(historialClinicoRepository.save(historial));
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE MAPEO
    // =========================================================

    /**
     * Convierte una entidad {@link HistorialClinico} en {@link HistorialClinicoAdminResponseDTO}.
     *
     * @param historial entidad a convertir
     * @return DTO con la vista completa para administrador
     */
    private HistorialClinicoAdminResponseDTO toAdminResponse(HistorialClinico historial) {
        Paciente p = historial.getPaciente();
        Usuario u = p != null ? p.getUsuario() : null;
        return new HistorialClinicoAdminResponseDTO(
                historial.getIdHistory(),
                p != null ? p.getIdPaciente() : null,
                u != null ? u.getNombre() : null,
                u != null ? u.getApellido() : null,
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }

    /**
     * Convierte una entidad {@link HistorialClinico} en {@link HistorialClinicoPsicologoResponseDTO}.
     *
     * @param historial entidad a convertir
     * @return DTO con la vista filtrada para psicólogo
     */
    private HistorialClinicoPsicologoResponseDTO toPsicologoResponse(HistorialClinico historial) {
        return new HistorialClinicoPsicologoResponseDTO(
                historial.getIdHistory(),
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }
}
