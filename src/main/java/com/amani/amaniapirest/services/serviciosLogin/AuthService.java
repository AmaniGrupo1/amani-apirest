package com.amani.amaniapirest.services.serviciosLogin;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PacientesRepository pacienteRepository;
    private final SecurityConfig securityConfig;
    private final JwtUtil jwtUtil;

    // ================= LOGIN =================
    public LoginResponseDTO login(LoginRequestDTO request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        var encoder = securityConfig.passwordEncoder();
        if (!encoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // UserDetails con autoridad correcta
        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        // Generar token JWT incluyendo el rol
        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token
        );
    }

    // ================= REGISTER PACIENTE =================
    public LoginResponseDTO registerPaciente(PacienteRequestDTO request) {

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getUsuario().getNombre());
        usuario.setApellido(request.getUsuario().getApellido());
        usuario.setEmail(request.getUsuario().getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getUsuario().getPassword()));
        usuario.setRol(RolUsuario.paciente);
        usuario.setActivo(true);
        LocalDateTime now = LocalDateTime.now();
        usuario.setFechaRegistro(now);
        usuarioRepository.save(usuario);

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        pacienteRepository.save(paciente);

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token
        );
    }

    // ================= REGISTER ADMIN =================
    public LoginResponseDTO registerAdmin(RegistryRequestDTO request) {

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.admin);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token
        );
    }

    //Listo los administradores
    public List<AdministradorDTO> listarAdministradores() {
        List<Usuario> usuarios = usuarioRepository.findByRol(RolUsuario.admin);

        return usuarios.stream()
                .map(u -> new AdministradorDTO(
                        u.getIdUsuario(),
                        u.getNombre(),
                        u.getApellido(),
                        u.getEmail(),
                        u.getRol().name(),
                        u.getActivo()
                ))
                .toList();
    }

    // ================= REGISTER PSICOLOGO =================
    public LoginResponseDTO registerPsicologo(RegistryRequestDTO request) {

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.psicologo);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token
        );
    }

    // ================= BAJA =================
    public void darBajaPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findByUsuario_IdUsuario(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        paciente.getUsuario().setActivo(false);
        paciente.getUsuario().setFechaBaja(LocalDateTime.now());
        usuarioRepository.save(paciente.getUsuario());
    }
}