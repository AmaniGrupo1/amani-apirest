package com.amani.amaniapirest.services.profile;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.UserDetailsServiceImpl;
import com.amani.amaniapirest.dto.profile.*;
import com.amani.amaniapirest.dto.profile.admin.AdminDTO;
import com.amani.amaniapirest.dto.profile.admin.AdminResponseDTO;
import com.amani.amaniapirest.dto.profile.admin.UpdateAdminRequestDTO;
import com.amani.amaniapirest.dto.profile.paciente.PacienteDTO;
import com.amani.amaniapirest.dto.profile.paciente.PacienteResponseDTO;
import com.amani.amaniapirest.dto.profile.paciente.UpdatePacienteRequestDTO;
import com.amani.amaniapirest.dto.profile.paciente.UsuarioUpdateDTO;
import com.amani.amaniapirest.dto.profile.psicologo.PsicologoDTO;
import com.amani.amaniapirest.dto.profile.psicologo.UpdatePsicologoRequestDTO;
import com.amani.amaniapirest.dto.profile.psicologo.UpdatePsicologoResponseDTO;
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

    /**
     * Obtener perfil completo de psicólogo
     */
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

    //-------------------------------------------
    // ADMIN
    //-------------------------------------------

    @Transactional
    public AdminDTO getAdminProfile(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        if (!usuario.getRol().name().equals("admin")) {
            throw new RuntimeException("El usuario no es administrador");
        }

        return new AdminDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );
    }

    @Transactional
    public AdminResponseDTO updateAdminProfile(Long idUsuario, UpdateAdminRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        if (!usuario.getRol().name().equals("admin")) {
            throw new RuntimeException("El usuario no es administrador");
        }

        boolean emailChanged = false; // 👈 IMPORTANTE

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellido() != null) usuario.setApellido(dto.getApellido());

        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {

            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email ya registrado");
            }

            usuario.setEmail(dto.getEmail());
            emailChanged = true;
        }

        usuarioRepository.save(usuario);

        AdminDTO adminDTO = new AdminDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );

        String newToken = null;

        // 🔥 GENERAR TOKEN SOLO SI CAMBIA EMAIL
        if (emailChanged) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
            newToken = jwtUtil.generateToken(userDetails, usuario.getRol().name());
        }

        return new AdminResponseDTO(adminDTO, newToken);
    }


    public AdminDTO updateAdminPhoto(Long idUsuario, MultipartFile file) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        if (!usuario.getRol().name().equals("admin")) {
            throw new RuntimeException("El usuario no es administrador");
        }

        String fotoActual = usuario.getFotoPerfilUrl();
        if (fotoActual != null && !fotoActual.equals(Usuario.AVATAR_DEFAULT)) {
            fileStorageService.deleteFile(fotoActual);
        }

        String urlFoto = fileStorageService.storeFile(file);
        usuario.setFotoPerfilUrl(urlFoto);

        usuarioRepository.save(usuario);

        return new AdminDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );
    }
    //-------------------------------------------
    // PACIENTE
    //-------------------------------------------
    @Transactional
    public PacienteDTO getPacienteProfile(Long idPaciente) {

        Paciente paciente = pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = paciente.getUsuario();

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );

        return new PacienteDTO(
                paciente.getIdPaciente(),
                paciente.getTelefono(),
                paciente.getGenero(),
                paciente.getFechaNacimiento(),
                usuarioDTO
        );
    }

    @Transactional
    public PacienteResponseDTO updatePacienteProfile(Long idPaciente, UpdatePacienteRequestDTO dto) {

        Paciente paciente = pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = paciente.getUsuario();

        boolean emailChanged = false; // 👈 IMPORTANTE

        if (dto.getTelefono() != null) paciente.setTelefono(dto.getTelefono());
        if (dto.getGenero() != null) paciente.setGenero(dto.getGenero());
        if (dto.getFechaNacimiento() != null) paciente.setFechaNacimiento(dto.getFechaNacimiento());

        if (dto.getUsuario() != null) {
            UsuarioUpdateDTO u = dto.getUsuario();

            if (u.getNombre() != null) usuario.setNombre(u.getNombre());
            if (u.getApellido() != null) usuario.setApellido(u.getApellido());

            if (u.getEmail() != null && !u.getEmail().equals(usuario.getEmail())) {

                if (usuarioRepository.existsByEmail(u.getEmail())) {
                    throw new RuntimeException("Email ya registrado");
                }

                usuario.setEmail(u.getEmail());
                emailChanged = true;
            }
        }

        usuarioRepository.save(usuario);
        Paciente saved = pacientesRepository.save(paciente);

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );

        PacienteDTO pacienteDTO = new PacienteDTO(
                saved.getIdPaciente(),
                saved.getTelefono(),
                saved.getGenero(),
                saved.getFechaNacimiento(),
                usuarioDTO
        );

        String newToken = null;

        // 🔥 GENERAR TOKEN SOLO SI CAMBIA EMAIL
        if (emailChanged) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
            newToken = jwtUtil.generateToken(userDetails, usuario.getRol().name());
        }

        return new PacienteResponseDTO(pacienteDTO, newToken);
    }


    public PacienteDTO updatePacientePhoto(Long idPaciente, MultipartFile file) {

        Paciente paciente = pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = paciente.getUsuario();

        String fotoActual = usuario.getFotoPerfilUrl();
        if (fotoActual != null && !fotoActual.equals(Usuario.AVATAR_DEFAULT)) {
            fileStorageService.deleteFile(fotoActual);
        }

        String urlFoto = fileStorageService.storeFile(file);
        usuario.setFotoPerfilUrl(urlFoto);

        usuarioRepository.save(usuario);

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl()
        );

        return new PacienteDTO(
                paciente.getIdPaciente(),
                paciente.getTelefono(),
                paciente.getGenero(),
                paciente.getFechaNacimiento(),
                usuarioDTO
        );
    }
}
