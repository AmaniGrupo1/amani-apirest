package com.amani.amaniapirest.services.psicologo;


import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PsicologoSelfService {

    private final PsicologoRepository psicologoRepository;
    private final UsuarioRepository usuarioRepository;

    public PsicologoSelfService(PsicologoRepository psicologoRepository, UsuarioRepository usuarioRepository) {
        this.psicologoRepository = psicologoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<PsicologoSelfResponseDTO> findAll() {
        return psicologoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PsicologoSelfResponseDTO findById(Long idPsicologo) {
        return toResponse(getPsicologoOrThrow(idPsicologo));
    }

    public PsicologoSelfResponseDTO create(PsicologoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + request.getIdUsuario()));

        Psicologo psicologo = new Psicologo();
        psicologo.setUsuario(usuario);
        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia());
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setCreatedAt(LocalDateTime.now());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toResponse(psicologoRepository.save(psicologo));
    }

    public PsicologoSelfResponseDTO update(Long idPsicologo, PsicologoRequestDTO request) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);

        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia());
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toResponse(psicologoRepository.save(psicologo));
    }

    public void delete(Long idPsicologo) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        psicologoRepository.delete(psicologo);
    }

    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }

    private PsicologoSelfResponseDTO toResponse(Psicologo psicologo) {
        return new PsicologoSelfResponseDTO(
                psicologo.getIdPsicologo(),
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia()
        );
    }
}
