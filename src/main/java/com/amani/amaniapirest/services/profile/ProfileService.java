package com.amani.amaniapirest.services.profile;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.UserDetailsServiceImpl;
import com.amani.amaniapirest.dto.profile.*;
import com.amani.amaniapirest.mappers.ProfileMapper;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileMapper profileMapper;
    private final UsuarioRepository usuarioRepository;
    private final PsicologoRepository psicologoRepository;
    private final PacientesRepository pacientesRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
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

    @Transactional
    public UpdatePsicologoResponseDTO updatePsicologoProfile(Long idPsicologo, UpdatePsicologoRequestDTO dto) {

        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Psicólogo no encontrado"));

        Usuario usuario = psicologo.getUsuario();

        boolean emailChanged = false; // 👈 DETECTAR CAMBIO

        if (dto.getUsuario() != null) {
            UsuarioUpdateDTO u = dto.getUsuario();

            if (u.getNombre() != null) usuario.setNombre(u.getNombre());
            if (u.getApellido() != null) usuario.setApellido(u.getApellido());

            if (u.getEmail() != null && !u.getEmail().equals(usuario.getEmail())) {

                if (usuarioRepository.existsByEmail(u.getEmail())) {
                    throw new RuntimeException("El email ya está registrado");
                }

                usuario.setEmail(u.getEmail());
                emailChanged = true; // 👈 IMPORTANTE
            }
        }

        if (dto.getEspecialidad() != null) psicologo.setEspecialidad(dto.getEspecialidad());
        if (dto.getExperiencia() != null) psicologo.setExperiencia(dto.getExperiencia());
        if (dto.getDescripcion() != null) psicologo.setDescripcion(dto.getDescripcion());
        if (dto.getLicencia() != null) psicologo.setLicencia(dto.getLicencia());

        usuarioRepository.save(usuario);
        Psicologo saved = psicologoRepository.save(psicologo);

        PsicologoDTO dtoResponse = profileMapper.toPsicologoDTO(saved);

        String newToken = null;

        // 🔥 SOLO SI CAMBIA EMAIL
        if (emailChanged) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
            newToken = jwtUtil.generateToken(userDetails, usuario.getRol().name());
        }

        return new UpdatePsicologoResponseDTO(dtoResponse, newToken);
    }
}
