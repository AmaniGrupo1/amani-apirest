package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.PacienteResponseDTO;
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
 * <p>Gestiona el ciclo de vida del perfil clínico del paciente, validando
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
     * Busca un paciente por su identificador único.
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
     * @param request {@link PacienteRequestDTO} con la información del paciente a crear
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


    public void delete(Long idPaciente) {
        Paciente paciente = getPacienteOrThrow(idPaciente);
        pacientesRepository.delete(paciente);
    }


    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }


    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }


    private PacienteResponseDTO toResponse(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getFechaNacimiento(),
                paciente.getGenero(),
                paciente.getTelefono(),
                paciente.getCreatedAt(),
                paciente.getUpdatedAt()
        );
    }
}

