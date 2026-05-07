package com.amani.amaniapirest.services.paciente;

import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.historialClinico.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.HistorialClinico;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import com.amani.amaniapirest.repository.hostorialClinico.HistorialClinicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoRepository psicologoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;

    /**
     * Obtiene todos los historiales clínicos.
     */
    public List<HistorialClinicoResponseDTO> findAll() {
        return historialClinicoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene un historial clínico por ID.
     */
    public HistorialClinicoResponseDTO findById(Long idHistory) {
        return toResponse(getHistorialOrThrow(idHistory));
    }

    /**
     * Obtiene el historial clínico de un paciente
     * validando permisos según rol.
     */
    public List<HistorialClinicoResponseDTO> findByPaciente(Long idPaciente) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        // =========================
        // ADMIN
        // =========================
        if (usuario.getRol() == RolUsuario.admin) {

            return historialClinicoRepository
                    .findByPacienteIdPaciente(idPaciente)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        // =========================
        // PACIENTE
        // =========================
        if (usuario.getRol() == RolUsuario.paciente) {

            Paciente paciente = pacientesRepository
                    .findByUsuario_Email(email)
                    .orElseThrow(() ->
                            new RuntimeException("Paciente no encontrado"));

            // impedir acceso a historiales ajenos
            if (!paciente.getIdPaciente().equals(idPaciente)) {
                throw new RuntimeException("No autorizado");
            }

            return historialClinicoRepository
                    .findByPacienteIdPaciente(idPaciente)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        // =========================
        // PSICOLOGO
        // =========================
        if (usuario.getRol() == RolUsuario.psicologo) {

            Psicologo psicologo = psicologoRepository
                    .findByUsuario_Email(email)
                    .orElseThrow(() ->
                            new RuntimeException("Psicólogo no encontrado"));

            boolean asignado =
                    psicologoPacienteRepository
                            .existsByPsicologo_IdPsicologoAndPaciente_IdPacienteAndFechaFinIsNull(
                                    psicologo.getIdPsicologo(),
                                    idPaciente
                            );

            // impedir acceso a pacientes no asignados
            if (!asignado) {
                throw new RuntimeException("No autorizado");
            }

            return historialClinicoRepository
                    .findByPacienteIdPaciente(idPaciente)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        throw new RuntimeException("No autorizado");
    }

    /**
     * Crear historial clínico.
     */
    public HistorialClinicoResponseDTO create(HistorialClinicoRequestDTO request) {

        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        HistorialClinico historial = new HistorialClinico();

        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toResponse(
                historialClinicoRepository.save(historial)
        );
    }

    /**
     * Actualizar historial clínico.
     */
    public HistorialClinicoResponseDTO update(
            Long idHistory,
            HistorialClinicoRequestDTO request
    ) {

        HistorialClinico historial = getHistorialOrThrow(idHistory);

        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toResponse(
                historialClinicoRepository.save(historial)
        );
    }

    /**
     * Eliminar historial clínico.
     */
    public void delete(Long idHistory) {
        historialClinicoRepository.delete(
                getHistorialOrThrow(idHistory)
        );
    }

    /**
     * Busca historial o lanza excepción.
     */
    private HistorialClinico getHistorialOrThrow(Long idHistory) {

        return historialClinicoRepository.findById(idHistory)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Historial clínico no encontrado con id: "
                                        + idHistory
                        ));
    }

    /**
     * Busca paciente o lanza excepción.
     */
    private Paciente getPacienteOrThrow(Long idPaciente) {

        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Paciente no encontrado con id: "
                                        + idPaciente
                        ));
    }

    /**
     * Convierte entidad a DTO.
     */
    private HistorialClinicoResponseDTO toResponse(
            HistorialClinico historial
    ) {

        return new HistorialClinicoResponseDTO(
                historial.getIdHistory(),
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }
}