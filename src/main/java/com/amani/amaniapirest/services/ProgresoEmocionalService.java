package com.amani.amaniapirest.services;


import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.ProgresoEmocionalResponseDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.ProgresoEmocional;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.ProgresoEmocionalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar el progreso emocional de los pacientes.
 *
 * <p>Permite crear, consultar, actualizar y eliminar registros de progreso
 * emocional, validando la existencia del paciente referenciado.</p>
 */
@Service
public class ProgresoEmocionalService {

    private final ProgresoEmocionalRepository progresoEmocionalRepository;
    private final PacientesRepository pacientesRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param progresoEmocionalRepository repositorio JPA de {@link ProgresoEmocional}
     * @param pacientesRepository         repositorio JPA de {@link Paciente}
     */
    public ProgresoEmocionalService(ProgresoEmocionalRepository progresoEmocionalRepository,
                                    PacientesRepository pacientesRepository) {
        this.progresoEmocionalRepository = progresoEmocionalRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /**
     * Obtiene la lista completa de registros de progreso emocional.
     *
     * @return lista de {@link ProgresoEmocionalResponseDTO} con todos los registros
     */
    public List<ProgresoEmocionalResponseDTO> findAll() {
        return progresoEmocionalRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un registro de progreso emocional por su identificador único.
     *
     * @param idProgress identificador del registro
     * @return {@link ProgresoEmocionalResponseDTO} con los datos encontrados
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    public ProgresoEmocionalResponseDTO findById(Long idProgress) {
        return toResponse(getProgresoOrThrow(idProgress));
    }

    /**
     * Obtiene todos los registros de progreso emocional de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link ProgresoEmocionalResponseDTO} del paciente indicado
     */
    public List<ProgresoEmocionalResponseDTO> findByPaciente(Long idPaciente) {
        return progresoEmocionalRepository.findByPaciente_IdPaciente(idPaciente)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Crea un nuevo registro de progreso emocional para un paciente.
     *
     * @param request {@link ProgresoEmocionalRequestDTO} con los datos del registro
     * @return {@link ProgresoEmocionalResponseDTO} con los datos creados
     * @throws RuntimeException si el paciente referenciado no existe
     */
    public ProgresoEmocionalResponseDTO create(ProgresoEmocionalRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        ProgresoEmocional progreso = new ProgresoEmocional();
        progreso.setPaciente(paciente);
        progreso.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        progreso.setNivelEstres(request.getNivelEstres() != null ? request.getNivelEstres() : 0);
        progreso.setNivelAnsiedad(request.getNivelAnsiedad() != null ? request.getNivelAnsiedad() : 0);
        progreso.setNivelAnimo(request.getNivelAnimo() != null ? request.getNivelAnimo() : 0);
        progreso.setCreatedAt(LocalDateTime.now());

        return toResponse(progresoEmocionalRepository.save(progreso));
    }

    /**
     * Actualiza un registro de progreso emocional existente.
     *
     * @param idProgress identificador del registro a actualizar
     * @param request    {@link ProgresoEmocionalRequestDTO} con los nuevos datos
     * @return {@link ProgresoEmocionalResponseDTO} con los datos actualizados
     * @throws RuntimeException si el registro o el paciente referenciado no existen
     */
    public ProgresoEmocionalResponseDTO update(Long idProgress, ProgresoEmocionalRequestDTO request) {
        ProgresoEmocional progreso = getProgresoOrThrow(idProgress);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        progreso.setPaciente(paciente);
        progreso.setFecha(request.getFecha() != null ? request.getFecha() : progreso.getFecha());
        if (request.getNivelEstres() != null) progreso.setNivelEstres(request.getNivelEstres());
        if (request.getNivelAnsiedad() != null) progreso.setNivelAnsiedad(request.getNivelAnsiedad());
        if (request.getNivelAnimo() != null) progreso.setNivelAnimo(request.getNivelAnimo());

        return toResponse(progresoEmocionalRepository.save(progreso));
    }

    /**
     * Elimina el registro de progreso emocional con el identificador indicado.
     *
     * @param idProgress identificador del registro a eliminar
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    public void delete(Long idProgress) {
        progresoEmocionalRepository.delete(getProgresoOrThrow(idProgress));
    }

    /**
     * Recupera un registro de progreso por id o lanza excepción si no existe.
     *
     * @param idProgress identificador del registro
     * @return entidad {@link ProgresoEmocional} encontrada
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    private ProgresoEmocional getProgresoOrThrow(Long idProgress) {
        return progresoEmocionalRepository.findById(idProgress)
                .orElseThrow(() -> new RuntimeException("Progreso emocional no encontrado con id: " + idProgress));
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
     * Convierte una entidad {@link ProgresoEmocional} en su DTO de respuesta.
     *
     * @param progreso entidad a convertir
     * @return {@link ProgresoEmocionalResponseDTO} con los datos mapeados
     */
    private ProgresoEmocionalResponseDTO toResponse(ProgresoEmocional progreso) {
        return new ProgresoEmocionalResponseDTO(
                progreso.getIdProgress(),
                progreso.getPaciente() != null ? progreso.getPaciente().getIdPaciente() : null,
                progreso.getFecha(),
                progreso.getNivelEstres(),
                progreso.getNivelAnsiedad(),
                progreso.getNivelAnimo(),
                progreso.getCreatedAt()
        );
    }
}

