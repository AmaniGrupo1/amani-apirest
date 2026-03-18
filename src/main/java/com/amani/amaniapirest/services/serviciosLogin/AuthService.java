package com.amani.amaniapirest.services.serviciosLogin;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.UserDetailsServiceImpl;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación y registro de usuarios.
 *
 * <p>Gestiona el inicio de sesión verificando credenciales y generando un token JWT,
 * así como el registro de nuevos usuarios con los distintos roles del sistema.</p>
 */
@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SecurityConfig securityConfig;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Autentica al usuario con email y contraseña, y devuelve un JWT si las credenciales son válidas.
     *
     * @throws RuntimeException si el email no existe o la contraseña no coincide
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!securityConfig.passwordEncoder().matches(request.getPassword(), usuario.getPassword())) {
            log.error("Contraseña incorrecta para: " + request.getEmail());
            throw new RuntimeException("Credenciales incorrectas");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name(), token);
    }

    /** Registra un nuevo paciente y devuelve su JWT. */
    public LoginResponseDTO registerPaciente(RegistryRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre("John");
        usuario.setApellido("Doe");
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.paciente);
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name(), token);
    }

    /** Registra un nuevo administrador y devuelve su JWT. */
    public LoginResponseDTO registerAdmin(RegistryRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.admin);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name(), token);
    }

    /** Registra un nuevo psicólogo y devuelve su JWT. */
    public LoginResponseDTO registerPsicologo(RegistryRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.psicologo);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name(), token);
    }
}
