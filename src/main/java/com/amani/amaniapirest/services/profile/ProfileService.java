package com.amani.amaniapirest.services.profile;

import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.mappers.ProfileMapper;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
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

        return profileMapper.toPsicologoDTO(psicologo);
    }

    /** Obtener perfil completo de psicólogo */
    public PsicologoDTO getProfile(Long idPsicologo) {
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));
        return profileMapper.toPsicologoDTO(psicologo);
    }
}
