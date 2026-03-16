package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PsicologoAdminService {

    private final PsicologoRepository psicologoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    public List<PsicologoAdminResponseDTO> findAll() {
        return psicologoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PsicologoAdminResponseDTO findById(Long idPsicologo) {
        return toResponse(getPsicologoOrThrow(idPsicologo));
    }

    public PsicologoAdminResponseDTO create(PsicologoRequestDTO request) {
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

    public PsicologoAdminResponseDTO update(Long idPsicologo, PsicologoRequestDTO request) {
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

    public List<PsicologoAdminResponseDTO> getPacientesConPsicologo() {
       return citaRepository.getPacientesConPsicologo();
    }

    private PsicologoAdminResponseDTO toResponse(Psicologo psicologo) {
        Usuario usuario = psicologo.getUsuario();
        return new PsicologoAdminResponseDTO(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                psicologo.getUpdatedAt()
        );
    }
}