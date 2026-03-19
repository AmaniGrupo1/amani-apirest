package com.amani.amaniapirest.services.serviciosLogin;



import com.amani.amaniapirest.configuration.SecurityConfig;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SecurityConfig securityConfig;
    private final PacientesRepository pacienteRepository;

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
                usuario.getRol().name(),
                null
        );
    }

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

        // 4. Respuesta
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                null
        );
    }


    public void darBajaPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findByUsuario_IdUsuario(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Dar de baja
        paciente.getUsuario().setActivo(false);
        paciente.getUsuario().setFechaBaja(LocalDateTime.now());

        // Guardar cambios
        usuarioRepository.save(paciente.getUsuario());
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

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name(), null);
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

        return new LoginResponseDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getRol().name(), null);
    }

}
