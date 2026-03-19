package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de administración para gestionar todos los perfiles de paciente del sistema.
 */
@Service
public class PacienteAdminService {

    private final PacientesRepository pacientesRepository;
    private final UsuarioRepository usuarioRepository;

    public PacienteAdminService(PacientesRepository pacientesRepository,
                                UsuarioRepository usuarioRepository) {
        this.pacientesRepository = pacientesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<PacienteAdminResponseDTO> findAll() {
        return pacientesRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PacienteAdminResponseDTO findById(Long idPaciente) {
        return toResponse(getPacienteOrThrow(idPaciente));
    }

    public PacienteAdminResponseDTO create(PacienteRequestDTO request) {
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());

        return toResponse(pacientesRepository.save(paciente));
    }

    /**
     * Actualizar paciente
     */
    public PacienteAdminResponseDTO update(Long idPaciente, PacienteRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(idPaciente);
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());

        return toResponse(pacientesRepository.save(paciente));
    }

    /**
     * Eliminar paciente
     */
    public void delete(Long idPaciente) {
        Paciente paciente = getPacienteOrThrow(idPaciente);
        pacientesRepository.delete(paciente);
    }

    /**
     * ------------------- Helpers -------------------
     */
    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    private PacienteAdminResponseDTO toResponse(Paciente paciente) {
        return new PacienteAdminResponseDTO(
                paciente.getIdPaciente(),
                paciente.getUsuario().getNombre(),
                paciente.getUsuario().getApellido(),
                paciente.getUsuario().getEmail(),
                paciente.getFechaNacimiento(),
                paciente.getGenero(),
                paciente.getTelefono(),
                paciente.getCreatedAt(),
                paciente.getUpdatedAt(),
                paciente.getUsuario().getActivo()
        );
    }
}
