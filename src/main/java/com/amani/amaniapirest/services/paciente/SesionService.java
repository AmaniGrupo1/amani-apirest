package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.SesionResponseDTO;
import com.amani.amaniapirest.models.Cita;
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


    private Sesion getSesionOrThrow(Long idSesion) {
        return sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + idSesion));
    }

    private Cita getCitaOrThrow(Long idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + idCita));
    }


    private SesionResponseDTO toResponse(Sesion sesion) {
        return new SesionResponseDTO(
                sesion.getSessionDate(),
                sesion.getDurationMinutes(),
                sesion.getNotas(),
                sesion.getRecomendaciones()
        );
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
        return sesionRepository.findByPaciente_IdPaciente(idPaciente)
                .stream().map(this::toResponse).toList();
    }
}

