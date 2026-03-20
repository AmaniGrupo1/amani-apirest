package com.amani.amaniapirest.services.serviciosLogin;



import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.configuration.UserDetailsServiceImpl;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio de autenticacion y registro de usuarios del sistema.
 *
 * <p>Gestiona el login con JWT, el registro de pacientes (publico),
 * el registro de administradores y psicologos (protegido) y la baja de pacientes.</p>
 */
@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SecurityConfig securityConfig;
    private final PacientesRepository pacienteRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Autentica un usuario verificando email y contrasena, y genera un token JWT.
     *
     * @param request credenciales del usuario (email y password).
     * @return datos del usuario autenticado con token JWT.
     * @throws RuntimeException si el usuario no existe o la contrasena es incorrecta.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        var encoder = securityConfig.passwordEncoder();
        if (!encoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token
        );
    }

    /**
     * Registra un nuevo paciente creando su usuario y perfil clinico.
     *
     * @param request datos del paciente y su usuario.
     * @return datos del paciente registrado con token JWT.
     */
    public LoginResponseDTO registerPaciente(PacienteRequestDTO request) {

        // 1. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getUsuario().getNombre());
        usuario.setApellido(request.getUsuario().getApellido());
        usuario.setEmail(request.getUsuario().getEmail());
        usuario.setPassword(securityConfig.passwordEncoder()
                .encode(request.getUsuario().getPassword()));
        usuario.setRol(RolUsuario.paciente);
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
//        usuario.setFechaBaja(LocalDateTime.now());

        usuarioRepository.save(usuario);

        // 2. Crear paciente
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());

        // 3. Guardar paciente
        pacienteRepository.save(paciente);

        // 4. Generar token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        // 5. Respuesta
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token
        );
    }


    /**
     * Da de baja a un paciente desactivando su cuenta de usuario
     * y registrando la fecha de baja.
     *
     * @param idPaciente identificador del usuario del paciente.
     * @throws RuntimeException si el paciente no existe.
     */
    public void darBajaPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findByUsuario_IdUsuario(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Dar de baja
        paciente.getUsuario().setActivo(false);
        paciente.getUsuario().setFechaBaja(LocalDateTime.now());

        // Guardar cambios
        usuarioRepository.save(paciente.getUsuario());
    }

    /**
     * Registra un nuevo usuario administrador.
     *
     * @param request datos del nuevo admin (nombre, apellido, email, password).
     * @return datos del admin registrado con token JWT.
     */
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

    /**
     * Registra un nuevo usuario psicologo.
     *
     * @param request datos del nuevo psicologo (nombre, apellido, email, password).
     * @return datos del psicologo registrado con token JWT.
     */
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
