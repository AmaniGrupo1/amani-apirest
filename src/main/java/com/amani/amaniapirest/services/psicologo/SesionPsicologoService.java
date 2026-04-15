package com.amani.amaniapirest.services.psicologo;


import com.amani.amaniapirest.dto.dtoPaciente.request.SesionRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO;
import com.amani.amaniapirest.models.Sesion;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.SesionRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para gestionar sesiones terapéuticas desde la perspectiva del psicólogo.
 *
 * @see com.amani.amaniapirest.dto.dtoPsicologo.response.SesionPsicologoResponseDTO
 */
@Service
public class SesionPsicologoService {

    private final SesionRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;

    public SesionPsicologoService(SesionRepository sesionRepository, UsuarioRepository usuarioRepository) {
        this.sesionRepository = sesionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<SesionPsicologoResponseDTO> findAllByPsicologo(Long idPsicologo) {
        return sesionRepository.findByPsicologo_IdPsicologo(idPsicologo)
                .stream().map(this::toResponse).toList();
    }

    public SesionPsicologoResponseDTO findById(Long idSesion) {
        return toResponse(getSesionOrThrow(idSesion));
    }

    public SesionPsicologoResponseDTO create(SesionRequestDTO request, Long idPsicologo) {
        Usuario psicologo = usuarioRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        Sesion sesion = new Sesion();
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes());
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());

        return toResponse(sesionRepository.save(sesion));
    }

    public SesionPsicologoResponseDTO update(Long idSesion, SesionRequestDTO request) {
        Sesion sesion = getSesionOrThrow(idSesion);
        sesion.setSessionDate(request.getSessionDate());
        sesion.setDurationMinutes(request.getDurationMinutes());
        sesion.setNotas(request.getNotas());
        sesion.setRecomendaciones(request.getRecomendaciones());
        return toResponse(sesionRepository.save(sesion));
    }

    public void delete(Long idSesion) {
        sesionRepository.delete(getSesionOrThrow(idSesion));
    }

    private Sesion getSesionOrThrow(Long idSesion) {
        return sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + idSesion));
    }

    private SesionPsicologoResponseDTO toResponse(Sesion sesion) {
        return new SesionPsicologoResponseDTO(
                sesion.getPaciente().getUsuario().getNombre(),
                sesion.getPaciente().getUsuario().getApellido(),
                sesion.getSessionDate(),
                sesion.getDurationMinutes(),
                sesion.getNotas(),
                sesion.getRecomendaciones()
        );
    }
}
