package com.amani.amaniapirest.services.serviciosLogin;



import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SecurityConfig securityConfig;
    public LoginResponseDTO login(LoginRequestDTO request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        var encoder = securityConfig.passwordEncoder();
        if (!encoder.matches(request.getPassword(), usuario.getPassword())) {
            log.error("Contraseña incorrecta");
        }

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name()
        );
    }

    public LoginResponseDTO registerPaciente(RegistryRequestDTO request) {

        Usuario usuario = new Usuario();
        usuario.setNombre("John");
        usuario.setApellido("Doe");
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.paciente);

        usuarioRepository.save(usuario);

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name()
        );
    }

    public LoginResponseDTO registerAdmin(RegistryRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.admin); // asigna rol de admin
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name());
    }

    public LoginResponseDTO registerPsicologo(RegistryRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.psicologo); // asigna rol de admin
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name());
    }
}
