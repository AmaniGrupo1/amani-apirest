package com.amani.amaniapirest.services.profile;

import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import com.amani.amaniapirest.mappers.ProfileMapper;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileMapper profileMapper;
    private final UsuarioRepository usuarioRepository;
    private final PsicologoRepository psicologoRepository;
    private final PacientesRepository pacientesRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;
    private final FileStorageService fileStorageService; // servicio para manejar almacenamiento de archivos


    public PsicologoDTO updateProfilePhoto(Long idPsicologo, MultipartFile file) {
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        Usuario usuario = psicologo.getUsuario();

        // Borrar foto anterior del disco solo si NO es el avatar por defecto
        String fotoActual = usuario.getFotoPerfilUrl();
        if (fotoActual != null && !fotoActual.equals(Usuario.AVATAR_DEFAULT)) {
            fileStorageService.deleteFile(fotoActual);
        }

        String urlFoto = fileStorageService.storeFile(file);
        usuario.setFotoPerfilUrl(urlFoto);

        usuarioRepository.save(usuario);

        psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        return profileMapper.toPsicologoDTO(psicologo);
    }

    /** Obtener perfil completo de psicólogo */
    public PsicologoDTO getProfile(Long idPsicologo) {
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));
        return profileMapper.toPsicologoDTO(psicologo);
    }


    public PsicologoDTO obtenerPsicologoAsignado(Long idPaciente) {

        PsicologoPaciente pp = psicologoPacienteRepository
                .findByPacienteIdPacienteAndFechaFinIsNull(idPaciente)
                .orElse(null);

        if (pp == null) return null;

        Psicologo psicologo = pp.getPsicologo();
        Usuario usuario = psicologo.getUsuario();

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );

        return new PsicologoDTO(
                psicologo.getIdPsicologo(),
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia(),
                usuarioDTO
        );
    }
}
