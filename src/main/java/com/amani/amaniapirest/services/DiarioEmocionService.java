package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoPaciente.request.DiarioEmocionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DiarioEmocionResponseDTO;
import com.amani.amaniapirest.models.DiarioEmocion;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.DiarioEmocionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar las entradas del diario emocional de pacientes.
 *
 * <p>Permite crear, consultar, actualizar y eliminar registros emocionales,
 * validando la existencia del paciente referenciado.</p>
 */
@Service
public class DiarioEmocionService {

    private final DiarioEmocionRepository diarioEmocionRepository;
    private final PacientesRepository pacientesRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param diarioEmocionRepository repositorio JPA de {@link DiarioEmocion}
     * @param pacientesRepository     repositorio JPA de {@link Paciente}
     */
    public DiarioEmocionService(DiarioEmocionRepository diarioEmocionRepository,
                                PacientesRepository pacientesRepository) {
        this.diarioEmocionRepository = diarioEmocionRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /**
     * Obtiene la lista completa de entradas del diario emocional.
     *
     * @return lista de {@link DiarioEmocionResponseDTO} con todas las entradas
     */
    public List<DiarioEmocionResponseDTO> findAll() {
        return diarioEmocionRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca una entrada del diario por su identificador único.
     *
     * @param idDiario identificador de la entrada
     * @return {@link DiarioEmocionResponseDTO} con los datos encontrados
     * @throws RuntimeException si no existe una entrada con el id proporcionado
     */
    public DiarioEmocionResponseDTO findById(Long idDiario) {
        return toResponse(getDiarioOrThrow(idDiario));
    }

    /**
     * Obtiene todas las entradas del diario emocional de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link DiarioEmocionResponseDTO} del paciente indicado
     */
    public List<DiarioEmocionResponseDTO> findByPaciente(Long idPaciente) {
        return diarioEmocionRepository.findByPaciente_IdPaciente(idPaciente)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Crea una nueva entrada en el diario emocional de un paciente.
     *
     * @param request {@link DiarioEmocionRequestDTO} con los datos de la entrada
     * @return {@link DiarioEmocionResponseDTO} con los datos creados
     * @throws RuntimeException si el paciente referenciado no existe
     */
    public DiarioEmocionResponseDTO create(DiarioEmocionRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        DiarioEmocion entrada = new DiarioEmocion();
        entrada.setPaciente(paciente);
        entrada.setFecha(request.getFecha() != null ? request.getFecha() : LocalDateTime.now());
        entrada.setEmocion(request.getEmocion());
        entrada.setIntensidad(request.getIntensidad() != null ? request.getIntensidad() : 0);
        entrada.setNota(request.getNota());

        return toResponse(diarioEmocionRepository.save(entrada));
    }

    /**
     * Actualiza una entrada existente del diario emocional.
     *
     * @param idDiario identificador de la entrada a actualizar
     * @param request  {@link DiarioEmocionRequestDTO} con los nuevos datos
     * @return {@link DiarioEmocionResponseDTO} con los datos actualizados
     * @throws RuntimeException si la entrada o el paciente referenciado no existen
     */
    public DiarioEmocionResponseDTO update(Long idDiario, DiarioEmocionRequestDTO request) {
        DiarioEmocion entrada = getDiarioOrThrow(idDiario);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        entrada.setPaciente(paciente);
        entrada.setFecha(request.getFecha() != null ? request.getFecha() : entrada.getFecha());
        entrada.setEmocion(request.getEmocion());
        entrada.setIntensidad(request.getIntensidad() != null ? request.getIntensidad() : entrada.getIntensidad());
        entrada.setNota(request.getNota());

        return toResponse(diarioEmocionRepository.save(entrada));
    }

    /**
     * Elimina la entrada del diario con el identificador indicado.
     *
     * @param idDiario identificador de la entrada a eliminar
     * @throws RuntimeException si no existe una entrada con el id proporcionado
     */
    public void delete(Long idDiario) {
        diarioEmocionRepository.delete(getDiarioOrThrow(idDiario));
    }

    /**
     * Recupera una entrada del diario por id o lanza excepción si no existe.
     *
     * @param idDiario identificador de la entrada
     * @return entidad {@link DiarioEmocion} encontrada
     * @throws RuntimeException si no existe una entrada con el id proporcionado
     */
    private DiarioEmocion getDiarioOrThrow(Long idDiario) {
        return diarioEmocionRepository.findById(idDiario)
                .orElseThrow(() -> new RuntimeException("Entrada de diario no encontrada con id: " + idDiario));
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
     * Convierte una entidad {@link DiarioEmocion} en su DTO de respuesta.
     *
     * @param entrada entidad a convertir
     * @return {@link DiarioEmocionResponseDTO} con los datos mapeados
     */
    private DiarioEmocionResponseDTO toResponse(DiarioEmocion entrada) {
        return new DiarioEmocionResponseDTO(
                entrada.getIdDiario(),
                entrada.getPaciente() != null ? entrada.getPaciente().getIdPaciente() : null,
                entrada.getFecha(),
                entrada.getEmocion(),
                entrada.getIntensidad(),
                entrada.getNota()
        );
    }
}

