package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.response.PacienteResponseDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para operaciones CRUD de pacientes.
 *
 * <p>Gestiona el ciclo de vida del perfil clinico del paciente, validando
 * la existencia del usuario vinculado y realizando el mapeo entre
 * entidades {@link Paciente} y DTOs de entrada/salida.</p>
 */
@Service
public class PacienteService {

    private final PacientesRepository pacientesRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construye el servicio inyectando sus repositorios.
     *
     * @param pacientesRepository repositorio JPA de {@link Paciente}
     * @param usuarioRepository   repositorio JPA de {@link Usuario}
     */
    public PacienteService(PacientesRepository pacientesRepository, UsuarioRepository usuarioRepository) {
        this.pacientesRepository = pacientesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene la lista completa de pacientes registrados.
     *
     * @return lista de {@link PacienteResponseDTO} con todos los pacientes
     */
    public List<PacienteResponseDTO> findAll() {
        return pacientesRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un paciente por su identificador unico.
     *
     * @param idPaciente identificador del paciente a buscar
     * @return {@link PacienteResponseDTO} con los datos del paciente encontrado
     * @throws RuntimeException si no existe un paciente con el id proporcionado
     */
    public PacienteResponseDTO findById(Long idPaciente) {
        return toResponse(getPacienteOrThrow(idPaciente));
    }

    /**
     * Crea un nuevo perfil de paciente a partir de los datos del request.
     *
     * @param request {@link PacienteRequestDTO} con la informacion del paciente a crear
     * @return {@link PacienteResponseDTO} con los datos del paciente creado
     * @throws RuntimeException si el usuario referenciado por {@code idUsuario} no existe
     */
    public PacienteResponseDTO create(PacienteRequestDTO request) {
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setCreatedAt(LocalDateTime.now());
        paciente.setUpdatedAt(LocalDateTime.now());

        return toResponse(pacientesRepository.save(paciente));
    }

    /**
     * Actualiza los datos de un paciente existente.
     *
     * @param idPaciente identificador del paciente a actualizar
     * @param request    {@link PacienteRequestDTO} con los nuevos datos del paciente
     * @return {@link PacienteResponseDTO} con los datos actualizados
     * @throws RuntimeException si el paciente o el usuario referenciado no existen
     */
    public PacienteResponseDTO update(Long idPaciente, PacienteRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(idPaciente);
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setUpdatedAt(LocalDateTime.now());

        return toResponse(pacientesRepository.save(paciente));
    }

    /**
     * Elimina el paciente con el identificador indicado.
     *
     * @param idPaciente identificador del paciente a eliminar
     * @throws RuntimeException si no existe un paciente con el id proporcionado
     */
    public void delete(Long idPaciente) {
        Paciente paciente = getPacienteOrThrow(idPaciente);
        pacientesRepository.delete(paciente);
    }

    /**
     * Recupera un paciente por id o lanza excepcion si no existe.
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
     * Recupera un usuario por id o lanza excepcion si no existe.
     *
     * @param idUsuario identificador del usuario
     * @return entidad {@link Usuario} encontrada
     * @throws RuntimeException si no existe un usuario con el id proporcionado
     */
    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    /**
     * Convierte una entidad {@link Paciente} en su DTO de respuesta.
     *
     * @param paciente entidad a convertir
     * @return {@link PacienteResponseDTO} con los datos mapeados
     */
    private PacienteResponseDTO toResponse(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getIdPaciente(),
                paciente.getUsuario() != null ? paciente.getUsuario().getIdUsuario() : null,
                paciente.getFechaNacimiento(),
                paciente.getGenero(),
                paciente.getTelefono(),
                paciente.getCreatedAt(),
                paciente.getUpdatedAt()
        );
    }
}

