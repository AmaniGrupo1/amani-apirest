package com.amani.amaniapirest.services.psicologo;


import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Direccion;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


/**
 * Servicio de solo lectura que permite al psicólogo consultar los pacientes asignados a él.
 */
@Service
public class PacientePsicologoService {

    private final PacientesRepository pacientesRepository;

    public PacientePsicologoService(PacientesRepository pacientesRepository) {
        this.pacientesRepository = pacientesRepository;
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
        PacientePsicologoResponseDTO dto = new PacientePsicologoResponseDTO();

        Usuario usuario = paciente.getUsuario();

        dto.setIdPaciente(paciente.getIdPaciente());
        dto.setNombre(usuario != null ? usuario.getNombre() : null);
        dto.setApellido(usuario != null ? usuario.getApellido() : null);
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setEmail(usuario != null ? usuario.getEmail() : null);
        dto.setGenero(paciente.getGenero());
        dto.setTelefono(paciente.getTelefono());
        dto.setDni(usuario != null ? usuario.getDni() : null);

        // Dirección: mapear la primera dirección disponible a DireccionResponseDTO
        List<Direccion> dirs = paciente.getDirecciones();
        if (dirs != null && !dirs.isEmpty()) {
            Direccion d = dirs.get(0);
            dto.setDireccion(new DireccionResponseDTO(
                    d.getCalle(),
                    d.getCiudad(),
                    d.getProvincia(),
                    d.getCodigoPostal(),
                    d.getPais()
            ));
        }

        // Hora inicio / fin: tomar la próxima cita pendiente o confirmada
        List<Cita> citas = paciente.getCitas();
        if (citas != null && !citas.isEmpty()) {
            citas.stream()
                    .filter(c -> c.getEstado().equals("pendiente") || c.getEstado().equals("confirmada"))
                    .min(Comparator.comparing(Cita::getStartDatetime)) // próxima cita
                    .ifPresent(cita -> {
                        LocalDateTime start = cita.getStartDatetime();
                        dto.setHoraInicio(start.toLocalTime());
                        dto.setHoraFin(start.plusMinutes(cita.getDurationMinutes()).toLocalTime());
                    });
        }

        return dto;
    }
}