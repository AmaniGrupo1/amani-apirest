package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoAdmin.response.SesionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.models.Sesion;
import com.amani.amaniapirest.repository.SesionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de administración para consultar y gestionar todas las sesiones terapéuticas.
 */
@Service
public class SesionAdminService {

    private final SesionRepository sesionRepository;

    public SesionAdminService(SesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    public List<SesionAdminResponseDTO> findAll() {
        return sesionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public SesionAdminResponseDTO findById(Long idSesion) {
        return toResponse(getSesionOrThrow(idSesion));
    }

    public SesionAdminResponseDTO create(SesionRequestDTO request) {
        Sesion sesion = new Sesion();
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes());
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setCreatedAt(LocalDateTime.now());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toResponse(sesionRepository.save(sesion));
    }

    public SesionAdminResponseDTO update(Long idSesion, SesionRequestDTO request) {
        Sesion sesion = getSesionOrThrow(idSesion);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes());
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        sesion.setUpdatedAt(LocalDateTime.now());

        return toResponse(sesionRepository.save(sesion));
    }

    public void delete(Long idSesion) {
        sesionRepository.delete(getSesionOrThrow(idSesion));
    }

    private Sesion getSesionOrThrow(Long idSesion) {
        return sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + idSesion));
    }

    private SesionAdminResponseDTO toResponse(Sesion sesion) {
        return new SesionAdminResponseDTO(
                sesion.getPaciente().getUsuario().getNombre(),
                sesion.getPaciente().getUsuario().getApellido(),
                sesion.getPsicologo().getUsuario().getNombre(),
                sesion.getPsicologo().getUsuario().getApellido(),
                sesion.getSessionDate(),
                sesion.getDurationMinutes(),
                sesion.getNotas(),
                sesion.getRecomendaciones(),
                sesion.getCreatedAt(),
                sesion.getUpdatedAt()
        );
    }
}
