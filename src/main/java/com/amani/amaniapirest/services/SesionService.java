package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoAdmin.request.SesionAdminRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.request.SesionPsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.SesionResponseDTO;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Sesion;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.SesionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para operaciones CRUD de sesiones terapéuticas.
 *
 * <p>Gestiona el ciclo de vida de una {@link Sesion} con métodos específicos
 * por rol: administrador, psicólogo y paciente. Cada rol accede únicamente
 * a los datos que le corresponden y recibe el DTO apropiado.</p>
 */
@Service
public class SesionService {

    private final SesionRepository sesionRepository;
    private final CitaRepository citaRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param sesionRepository repositorio JPA de {@link Sesion}
     * @param citaRepository   repositorio JPA de {@link Cita}
     */
    public SesionService(SesionRepository sesionRepository, CitaRepository citaRepository) {
        this.sesionRepository = sesionRepository;
        this.citaRepository = citaRepository;
    }

    /**
     * Obtiene la lista completa de sesiones registradas.
     *
     * @return lista de {@link SesionResponseDTO} con todas las sesiones
     */
    public List<SesionResponseDTO> findAll() {
        return sesionRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca una sesión por su identificador único.
     *
     * @param idSesion identificador de la sesión a buscar
     * @return {@link SesionResponseDTO} con los datos de la sesión encontrada
     * @throws RuntimeException si no existe una sesión con el id proporcionado
     */
    public SesionResponseDTO findById(Long idSesion) {
        return toResponse(getSesionOrThrow(idSesion));
    }

    /**
     * Crea una nueva sesión terapéutica a partir de los datos del request.
     * Los identificadores de paciente y psicólogo se resuelven desde la cita vinculada.
     *
     * @param request {@link SesionRequestDTO} con los datos de la sesión a crear
     * @return {@link SesionResponseDTO} con los datos de la sesión creada
     * @throws RuntimeException si la cita referenciada por {@code idCita} no existe
     */
    public SesionResponseDTO create(SesionRequestDTO request) {
        Cita cita = getCitaOrThrow(request.getIdCita());

        Sesion sesion = new Sesion();
        sesion.setCita(cita);
        sesion.setIdPaciente(cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null);
        sesion.setIdPsicologo(cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setCreatedAt(LocalDateTime.now());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toResponse(sesionRepository.save(sesion));
    }

    /**
     * Actualiza los datos de una sesión existente.
     *
     * @param idSesion identificador de la sesión a actualizar
     * @param request  {@link SesionRequestDTO} con los nuevos datos de la sesión
     * @return {@link SesionResponseDTO} con los datos actualizados
     * @throws RuntimeException si la sesión o la cita referenciada no existen
     */
    public SesionResponseDTO update(Long idSesion, SesionRequestDTO request) {
        Sesion sesion = getSesionOrThrow(idSesion);
        Cita cita = getCitaOrThrow(request.getIdCita());

        sesion.setCita(cita);
        sesion.setIdPaciente(cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null);
        sesion.setIdPsicologo(cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : sesion.getDurationMinutes());
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toResponse(sesionRepository.save(sesion));
    }

    /**
     * Elimina la sesión con el identificador indicado.
     *
     * @param idSesion identificador de la sesión a eliminar
     * @throws RuntimeException si no existe una sesión con el id proporcionado
     */
    public void delete(Long idSesion) {
        Sesion sesion = getSesionOrThrow(idSesion);
        sesionRepository.delete(sesion);
    }

    /**
     * Recupera una sesión por id o lanza excepción si no existe.
     *
     * @param idSesion identificador de la sesión
     * @return entidad {@link Sesion} encontrada
     * @throws RuntimeException si no existe una sesión con el id proporcionado
     */
    private Sesion getSesionOrThrow(Long idSesion) {
        return sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + idSesion));
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
     * Convierte una entidad {@link Sesion} en su DTO de respuesta.
     *
     * @param sesion entidad a convertir
     * @return {@link SesionResponseDTO} con los datos mapeados
     */
    private SesionResponseDTO toResponse(Sesion sesion) {
        return new SesionResponseDTO(
                sesion.getIdSesion(),
                sesion.getCita() != null ? sesion.getCita().getIdCita() : null,
                sesion.getSessionDate(),
                sesion.getDurationMinutes(),
                sesion.getNotas(),
                sesion.getRecomendaciones()
        );
    }

    // =========================================================
    // MÉTODOS PARA ROL: ADMIN
    // =========================================================

    /**
     * Obtiene la lista completa de sesiones (vista de administrador).
     *
     * @return lista de {@link SesionAdminResponseDTO} con todas las sesiones
     */
    public List<SesionAdminResponseDTO> findAllAdmin() {
        return sesionRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    /**
     * Busca una sesión por su id (vista de administrador).
     *
     * @param idSesion identificador de la sesión
     * @return {@link SesionAdminResponseDTO} con los datos completos
     * @throws RuntimeException si no existe la sesión
     */
    public SesionAdminResponseDTO findByIdAdmin(Long idSesion) {
        return toAdminResponse(getSesionOrThrow(idSesion));
    }

    /**
     * Crea una nueva sesión desde la vista de administrador.
     *
     * @param request {@link SesionAdminRequestDTO} con los datos de la sesión
     * @return {@link SesionAdminResponseDTO} con los datos de la sesión creada
     * @throws RuntimeException si la cita referenciada no existe
     */
    public SesionAdminResponseDTO createAdmin(SesionAdminRequestDTO request) {
        Cita cita = getCitaOrThrow(request.getIdCita());

        Sesion sesion = new Sesion();
        sesion.setCita(cita);
        sesion.setIdPaciente(cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null);
        sesion.setIdPsicologo(cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setCreatedAt(LocalDateTime.now());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toAdminResponse(sesionRepository.save(sesion));
    }

    /**
     * Actualiza una sesión existente desde la vista de administrador.
     *
     * @param idSesion identificador de la sesión a actualizar
     * @param request  {@link SesionAdminRequestDTO} con los nuevos datos
     * @return {@link SesionAdminResponseDTO} con los datos actualizados
     * @throws RuntimeException si la sesión o la cita no existen
     */
    public SesionAdminResponseDTO updateAdmin(Long idSesion, SesionAdminRequestDTO request) {
        Sesion sesion = getSesionOrThrow(idSesion);
        Cita cita = getCitaOrThrow(request.getIdCita());

        sesion.setCita(cita);
        sesion.setIdPaciente(cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null);
        sesion.setIdPsicologo(cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : sesion.getDurationMinutes());
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toAdminResponse(sesionRepository.save(sesion));
    }

    // =========================================================
    // MÉTODOS PARA ROL: PSICÓLOGO
    // =========================================================

    /**
     * Obtiene todas las sesiones realizadas por un psicólogo concreto.
     *
     * @param idPsicologo identificador del psicólogo autenticado
     * @return lista de {@link SesionPsicologoResponseDTO} con sus sesiones
     */
    public List<SesionPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {
        return sesionRepository.findByIdPsicologo(idPsicologo)
                .stream().map(this::toPsicologoResponse).toList();
    }

    /**
     * Crea una nueva sesión desde la perspectiva del psicólogo.
     *
     * @param request {@link SesionPsicologoRequestDTO} con los datos de la sesión
     * @return {@link SesionPsicologoResponseDTO} con los datos de la sesión creada
     * @throws RuntimeException si la cita referenciada no existe
     */
    public SesionPsicologoResponseDTO createPsicologo(SesionPsicologoRequestDTO request) {
        Cita cita = getCitaOrThrow(request.getIdCita());

        Sesion sesion = new Sesion();
        sesion.setCita(cita);
        sesion.setIdPaciente(cita.getPaciente() != null ? cita.getPaciente().getIdPaciente() : null);
        sesion.setIdPsicologo(cita.getPsicologo() != null ? cita.getPsicologo().getIdPsicologo() : null);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0);
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setCreatedAt(LocalDateTime.now());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toPsicologoResponse(sesionRepository.save(sesion));
    }

    // =========================================================
    // MÉTODOS PARA ROL: PACIENTE
    // =========================================================

    /**
     * Obtiene todas las sesiones de un paciente concreto.
     *
     * @param idPaciente identificador del paciente autenticado
     * @return lista de {@link SesionResponseDTO} con las sesiones del paciente
     */
    public List<SesionResponseDTO> findAllByPaciente(Long idPaciente) {
        return sesionRepository.findByIdPaciente(idPaciente)
                .stream().map(this::toResponse).toList();
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE MAPEO
    // =========================================================

    /**
     * Convierte una entidad {@link Sesion} en {@link SesionAdminResponseDTO}.
     *
     * @param sesion entidad a convertir
     * @return DTO con la vista completa para administrador
     */
    private SesionAdminResponseDTO toAdminResponse(Sesion sesion) {
        Cita cita = sesion.getCita();
        Paciente p = cita != null ? cita.getPaciente() : null;
        Psicologo ps = cita != null ? cita.getPsicologo() : null;
        return new SesionAdminResponseDTO(
                sesion.getIdSesion(),
                cita != null ? cita.getIdCita() : null,
                sesion.getIdPaciente(),
                p != null && p.getUsuario() != null ? p.getUsuario().getNombre() : null,
                p != null && p.getUsuario() != null ? p.getUsuario().getApellido() : null,
                sesion.getIdPsicologo(),
                ps != null && ps.getUsuario() != null ? ps.getUsuario().getNombre() : null,
                ps != null && ps.getUsuario() != null ? ps.getUsuario().getApellido() : null,
                sesion.getSessionDate(),
                sesion.getDurationMinutes(),
                sesion.getNotas(),
                sesion.getRecomendaciones(),
                sesion.getCreatedAt(),
                sesion.getUpdatedAt()
        );
    }

    /**
     * Convierte una entidad {@link Sesion} en {@link SesionPsicologoResponseDTO}.
     *
     * @param sesion entidad a convertir
     * @return DTO con la vista filtrada para psicólogo
     */
    private SesionPsicologoResponseDTO toPsicologoResponse(Sesion sesion) {
        Cita cita = sesion.getCita();
        Paciente p = cita != null ? cita.getPaciente() : null;
        return new SesionPsicologoResponseDTO(
                sesion.getIdSesion(),
                sesion.getIdPaciente(),
                p != null && p.getUsuario() != null ? p.getUsuario().getNombre() : null,
                p != null && p.getUsuario() != null ? p.getUsuario().getApellido() : null,
                sesion.getSessionDate(),
                sesion.getDurationMinutes(),
                sesion.getNotas(),
                sesion.getRecomendaciones()
        );
    }
}

