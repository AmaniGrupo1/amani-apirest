package com.amani.amaniapirest.services.psicologo;


import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacientePsicologoService {

    private final PacientesRepository pacientesRepository;
    private final UsuarioRepository usuarioRepository;

    public PacientePsicologoService(PacientesRepository pacientesRepository,
                                    UsuarioRepository usuarioRepository) {
        this.pacientesRepository = pacientesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Listar todos los pacientes
     */
    public List<PacientePsicologoResponseDTO> findAll() {
        return pacientesRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Buscar paciente por ID
     */
    public PacientePsicologoResponseDTO findById(Long idPaciente) {
        return toResponse(getPacienteOrThrow(idPaciente));
    }

    /**
     * Crear paciente (solo datos básicos, psicólogo no asigna usuario)
     */
    public PacientePsicologoResponseDTO create(PacienteRequestDTO request) {
        Paciente paciente = new Paciente();
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        return toResponse(pacientesRepository.save(paciente));
    }

    /**
     * Actualizar paciente
     */
    public PacientePsicologoResponseDTO update(Long idPaciente, PacienteRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(idPaciente);

        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setCitas(null); // No actualizamos citas desde aquí
        paciente.setUsuario(null); // No actualizamos usuario desde aquí
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

    private PacientePsicologoResponseDTO toResponse(Paciente paciente) {
        return new PacientePsicologoResponseDTO(
                paciente.getUsuario() != null ? paciente.getUsuario().getNombre() : null,
                paciente.getUsuario() != null ? paciente.getUsuario().getApellido() : null,
                paciente.getFechaNacimiento(),
                paciente.getGenero(),
                paciente.getTelefono()
        );
    }
}