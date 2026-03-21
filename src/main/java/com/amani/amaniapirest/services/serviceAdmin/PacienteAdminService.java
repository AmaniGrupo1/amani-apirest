package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de administración para gestionar todos los perfiles de paciente del sistema.
 */
@Service
@RequiredArgsConstructor
public class PacienteAdminService {

    private final PacientesRepository pacientesRepository;
    private final UsuarioRepository usuarioRepository;
    private final PsicologoRepository psicologoRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;


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

    @Transactional
    public boolean asignarPsicologo(Long pacienteId, Long psicologoId) {

        Paciente paciente = pacientesRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Psicologo psicologo = psicologoRepository.findById(psicologoId)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        // Cierra cualquier asignación anterior del paciente
        PsicologoPaciente asignacionActual = psicologoPacienteRepository
                .findByPacienteIdPacienteAndFechaFinIsNull(pacienteId);

        if (asignacionActual != null) {
            asignacionActual.setFechaFin(LocalDateTime.now());
            psicologoPacienteRepository.save(asignacionActual);
        }

        // Crear nueva asignación
        PsicologoPaciente nuevaAsignacion = new PsicologoPaciente();
        nuevaAsignacion.setPaciente(paciente);
        nuevaAsignacion.setPsicologo(psicologo);
        nuevaAsignacion.setFechaInicio(LocalDateTime.now());
        nuevaAsignacion.setFechaFin(null);
        System.out.println("Guardando asignación...");
        psicologoPacienteRepository.save(nuevaAsignacion);
        System.out.println("Asignación guardada");

        return true;
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
