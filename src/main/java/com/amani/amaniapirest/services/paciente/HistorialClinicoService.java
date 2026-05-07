package com.amani.amaniapirest.services.paciente;


import com.amani.amaniapirest.configuration.UserDetailsImpl;
import com.amani.amaniapirest.dto.dtoPaciente.request.HistorialClinicoRequestDTO;
import com.amani.amaniapirest.dto.historialClinico.HistorialClinicoResponseDTO;
import com.amani.amaniapirest.models.HistorialClinico;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.hostorialClinico.HistorialClinicoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar el historial clínico de los pacientes.
 *
 * <p>Permite crear, consultar, actualizar y eliminar registros clínicos,
 * validando la existencia del paciente referenciado en cada operación.</p>
 */
@Service
@RequiredArgsConstructor
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;



    /**
     * Obtiene la lista completa de registros del historial clínico.
     *
     * @return lista de {@link HistorialClinicoResponseDTO} con todos los registros
     */
    public List<HistorialClinicoResponseDTO> findAll() {
        return historialClinicoRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un registro clínico por su identificador único.
     *
     * @param idHistory identificador del registro
     * @return {@link HistorialClinicoResponseDTO} con los datos encontrados
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    public HistorialClinicoResponseDTO findById(Long idHistory) {
        return toResponse(getHistorialOrThrow(idHistory));
    }

    /**
     * Obtiene todos los registros del historial clínico de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de {@link HistorialClinicoResponseDTO} del paciente indicado
     */
    public List<HistorialClinicoResponseDTO> findByPaciente(
            Long idPaciente,
            Authentication authentication
    ) {

        boolean isPaciente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"));

        boolean isPsicologo = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PSICOLOGO"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 👤 PACIENTE → solo su historial
        if (isPaciente) {
            Long idSesion = ((UserDetailsImpl) authentication.getPrincipal()).getIdUsuario();

            if (!idSesion.equals(idPaciente)) {
                throw new RuntimeException("No puedes ver otro paciente");
            }
        }

        // 🧑‍⚕️ PSICOLOGO → validar relación real
        if (isPsicologo) {
            Long idPsicologo = ((UserDetailsImpl) authentication.getPrincipal()).getIdUsuario();

            boolean asignado = psicologoPacienteRepository
                    .existsByPacienteIdPacienteAndPsicologoIdPsicologoAndFechaFinIsNull(
                            idPaciente,
                            idPsicologo
                    );

            if (!asignado) {
                throw new RuntimeException("Paciente no asignado a este psicólogo");
            }
        }

        // 🛠 ADMIN → sin restricciones

        return historialClinicoRepository.findByPacienteIdPaciente(idPaciente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crea un nuevo registro en el historial clínico de un paciente.
     *
     * @param request {@link HistorialClinicoRequestDTO} con los datos del registro
     * @return {@link HistorialClinicoResponseDTO} con los datos creados
     * @throws RuntimeException si el paciente referenciado no existe
     */
    public HistorialClinicoResponseDTO create(HistorialClinicoRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(paciente);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toResponse(historialClinicoRepository.save(historial));
    }

    /**
     * Actualiza un registro clínico existente.
     *
     * @param idHistory identificador del registro a actualizar
     * @param request   {@link HistorialClinicoRequestDTO} con los nuevos datos
     * @return {@link HistorialClinicoResponseDTO} con los datos actualizados
     * @throws RuntimeException si el registro o el paciente referenciado no existen
     */
    public HistorialClinicoResponseDTO update(Long idHistory, HistorialClinicoRequestDTO request) {
        HistorialClinico historial = getHistorialOrThrow(idHistory);
        historial.setTitulo(request.getTitulo());
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setObservaciones(request.getObservaciones());

        return toResponse(historialClinicoRepository.save(historial));
    }

    /**
     * Elimina el registro clínico con el identificador indicado.
     *
     * @param idHistory identificador del registro a eliminar
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    public void delete(Long idHistory) {
        historialClinicoRepository.delete(getHistorialOrThrow(idHistory));
    }

    /**
     * Recupera un registro clínico por id o lanza excepción si no existe.
     *
     * @param idHistory identificador del registro
     * @return entidad {@link HistorialClinico} encontrada
     * @throws RuntimeException si no existe un registro con el id proporcionado
     */
    private HistorialClinico getHistorialOrThrow(Long idHistory) {
        return historialClinicoRepository.findById(idHistory)
                .orElseThrow(() -> new RuntimeException("Historial clínico no encontrado con id: " + idHistory));
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
     * Convierte una entidad {@link HistorialClinico} en su DTO de respuesta.
     *
     * @param historial entidad a convertir
     * @return {@link HistorialClinicoResponseDTO} con los datos mapeados
     */
    private HistorialClinicoResponseDTO toResponse(HistorialClinico historial) {
        return new HistorialClinicoResponseDTO(
                historial.getIdHistory(), // ✅ ahora sí coincide con el DTO
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }
}

