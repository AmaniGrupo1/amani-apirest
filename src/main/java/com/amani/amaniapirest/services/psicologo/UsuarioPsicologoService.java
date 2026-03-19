package com.amani.amaniapirest.services.psicologo;


import com.amani.amaniapirest.dto.dtoPsicologo.response.UsuarioPsicologoResponseDTO;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de solo lectura que expone los datos de perfil del usuario autenticado como psicólogo.
 */
@Service
public class UsuarioPsicologoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioPsicologoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioPsicologoResponseDTO findById(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return toResponse(usuario);
    }

    private UsuarioPsicologoResponseDTO toResponse(Usuario usuario) {
        return new UsuarioPsicologoResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getActivo()
        );
    }
}
