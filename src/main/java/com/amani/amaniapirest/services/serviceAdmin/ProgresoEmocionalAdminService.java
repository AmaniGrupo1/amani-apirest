package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoAdmin.response.ProgresoEmocionalAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.ProgresoEmocionalRequestDTO;
import com.amani.amaniapirest.models.ProgresoEmocional;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.ProgresoEmocionalRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProgresoEmocionalAdminService {

    private final ProgresoEmocionalRepository progresoRepository;
    private final PacientesRepository pacientesRepository;

    public ProgresoEmocionalAdminService(ProgresoEmocionalRepository progresoRepository,
                                         PacientesRepository pacientesRepository) {
        this.progresoRepository = progresoRepository;
        this.pacientesRepository = pacientesRepository;
    }

    public List<ProgresoEmocionalAdminResponseDTO> findAll() {
        return progresoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProgresoEmocionalAdminResponseDTO findById(Long idProgreso) {
        return toResponse(getProgresoOrThrow(idProgreso));
    }

    public ProgresoEmocionalAdminResponseDTO create(ProgresoEmocionalRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(request.getIdPaciente());

        ProgresoEmocional progreso = new ProgresoEmocional();
        progreso.setPaciente(paciente);
        progreso.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        progreso.setNivelEstres(request.getNivelEstres());
        progreso.setNivelAnsiedad(request.getNivelAnsiedad());
        progreso.setNivelAnimo(request.getNivelAnimo());

        return toResponse(progresoRepository.save(progreso));
    }

    public ProgresoEmocionalAdminResponseDTO update(Long idProgreso, ProgresoEmocionalRequestDTO request) {
        ProgresoEmocional progreso = getProgresoOrThrow(idProgreso);
        progreso.setFecha(request.getFecha() != null ? request.getFecha() : progreso.getFecha());
        progreso.setNivelEstres(request.getNivelEstres());
        progreso.setNivelAnsiedad(request.getNivelAnsiedad());
        progreso.setNivelAnimo(request.getNivelAnimo());

        return toResponse(progresoRepository.save(progreso));
    }

    public void delete(Long idProgreso) {
        ProgresoEmocional progreso = getProgresoOrThrow(idProgreso);
        progresoRepository.delete(progreso);
    }

    private ProgresoEmocional getProgresoOrThrow(Long idProgreso) {
        return progresoRepository.findById(idProgreso)
                .orElseThrow(() -> new RuntimeException("Progreso emocional no encontrado con id: " + idProgreso));
    }

    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private ProgresoEmocionalAdminResponseDTO toResponse(ProgresoEmocional progreso) {
        return new ProgresoEmocionalAdminResponseDTO(
                progreso.getPaciente().getUsuario().getNombre(),
                progreso.getFecha(),
                progreso.getNivelEstres(),
                progreso.getNivelAnsiedad(),
                progreso.getNivelAnimo(),
                progreso.getCreatedAt()
        );
    }
}