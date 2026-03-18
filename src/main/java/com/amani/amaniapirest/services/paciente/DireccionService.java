package com.amani.amaniapirest.services.paciente;


import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.models.Direccion;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.DireccionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servicio de negocio para gestionar las direcciones postales de los pacientes.
 */
@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final PacientesRepository pacientesRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param direccionRepository repositorio JPA de {@link Direccion}
     * @param pacientesRepository repositorio JPA de {@link Paciente}
     */
    public DireccionService(DireccionRepository direccionRepository, PacientesRepository pacientesRepository) {
        this.direccionRepository = direccionRepository;
        this.pacientesRepository = pacientesRepository;
    }

    /**
     * Obtiene la lista completa de direcciones registradas.
     *
     * @return lista de {@link DireccionResponseDTO} con todas las direcciones
     */
    public List<DireccionResponseDTO> findAll() {
        return direccionRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca una dirección por su identificador único.
     *
     * @param idDireccion identificador de la dirección
     * @return {@link DireccionResponseDTO} con los datos encontrados
     * @throws RuntimeException si no existe una dirección con el id proporcionado
     */
    public DireccionResponseDTO findById(Long idDireccion) {
        return toResponse(getDireccionOrThrow(idDireccion));
    }

    /**
     * Obtiene todas las direcciones de un paciente específico.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link DireccionResponseDTO} del paciente indicado
     */
    public List<DireccionResponseDTO> findByPaciente(Long idPaciente) {
        return direccionRepository.findByPaciente_IdPaciente(idPaciente)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Crea una nueva dirección para un paciente.
     *
     * @param request {@link DireccionRequestDTO} con los datos de la dirección
     * @return {@link DireccionResponseDTO} con los datos creados
     * @throws RuntimeException si el paciente referenciado no existe
     */
    public DireccionResponseDTO create(DireccionRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        Direccion direccion = new Direccion();
        direccion.setPaciente(paciente);
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setPais(request.getPais());
        direccion.setDescripcion(request.getDescripcion());

        return toResponse(direccionRepository.save(direccion));
    }

    /**
     * Actualiza los datos de una dirección existente.
     *
     * @param idDireccion identificador de la dirección a actualizar
     * @param request     {@link DireccionRequestDTO} con los nuevos datos
     * @return {@link DireccionResponseDTO} con los datos actualizados
     * @throws RuntimeException si la dirección o el paciente referenciado no existen
     */
    public DireccionResponseDTO update(Long idDireccion, DireccionRequestDTO request) {
        Direccion direccion = getDireccionOrThrow(idDireccion);
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        direccion.setPaciente(paciente);
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setPais(request.getPais());
        direccion.setDescripcion(request.getDescripcion());

        return toResponse(direccionRepository.save(direccion));
    }

    /**
     * Elimina la dirección con el identificador indicado.
     *
     * @param idDireccion identificador de la dirección a eliminar
     * @throws RuntimeException si no existe una dirección con el id proporcionado
     */
    public void delete(Long idDireccion) {
        direccionRepository.delete(getDireccionOrThrow(idDireccion));
    }

    /**
     * Recupera una dirección por id o lanza excepción si no existe.
     *
     * @param idDireccion identificador de la dirección
     * @return entidad {@link Direccion} encontrada
     * @throws RuntimeException si no existe una dirección con el id proporcionado
     */
    private Direccion getDireccionOrThrow(Long idDireccion) {
        return direccionRepository.findById(idDireccion)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + idDireccion));
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
     * Convierte una entidad {@link Direccion} en su DTO de respuesta.
     *
     * @param direccion entidad a convertir
     * @return {@link DireccionResponseDTO} con los datos mapeados
     */
    private DireccionResponseDTO toResponse(Direccion direccion) {
        return new DireccionResponseDTO(
                direccion.getCalle(),
                direccion.getCiudad(),
                direccion.getProvincia(),
                direccion.getCodigoPostal(),
                direccion.getPais(),
                direccion.getDescripcion()
        );
    }
}

