package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPsicologo.response.HistorialClinicoPsicologoResponseDTO;
import com.amani.amaniapirest.models.HistorialClinico;
import com.amani.amaniapirest.repository.hostorialClinico.HistorialClinicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para que el psicólogo gestione el historial clínico de sus pacientes.
 *
 * @see com.amani.amaniapirest.dto.dtoPsicologo.response.HistorialClinicoPsicologoResponseDTO
 */
@Service
public class HistorialClinicoPsicologoService {

    private final HistorialClinicoRepository historialClinicoRepository;

    public HistorialClinicoPsicologoService(HistorialClinicoRepository historialClinicoRepository) {
        this.historialClinicoRepository = historialClinicoRepository;
    }

    /** Lista todos los registros de los pacientes del psicólogo */
    public List<HistorialClinicoPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {
        return historialClinicoRepository.findByPacienteIdPaciente(idPsicologo)
                .stream()
                .map(this::toPsicologoResponse)
                .collect(Collectors.toList());
    }

    /** Obtiene un registro específico */
    public HistorialClinicoPsicologoResponseDTO findByIdPsicologo(Long idHistory) {
        HistorialClinico historial = historialClinicoRepository.findById(idHistory)
                .orElseThrow(() -> new RuntimeException("Historial clínico no encontrado con id: " + idHistory));
        return toPsicologoResponse(historial);
    }

    /** Actualiza solo las observaciones o diagnóstico (opcional según reglas de negocio) */
    public HistorialClinicoPsicologoResponseDTO updateObservaciones(Long idHistory, String diagnostico, String observaciones) {
        HistorialClinico historial = historialClinicoRepository.findById(idHistory)
                .orElseThrow(() -> new RuntimeException("Historial clínico no encontrado con id: " + idHistory));

        if (diagnostico != null) historial.setDiagnostico(diagnostico);
        if (observaciones != null) historial.setObservaciones(observaciones);

        return toPsicologoResponse(historialClinicoRepository.save(historial));
    }

    private HistorialClinicoPsicologoResponseDTO toPsicologoResponse(HistorialClinico historial) {
        return new HistorialClinicoPsicologoResponseDTO(
                historial.getTitulo(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCreadoEn()
        );
    }
}
