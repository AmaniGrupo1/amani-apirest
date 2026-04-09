package com.amani.amaniapirest.services.serviciosLogin;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.TutorRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.*;
import com.amani.amaniapirest.repository.*;
import com.amani.amaniapirest.services.ConsentimientoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.val;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PacientesRepository pacienteRepository;
    private final SituacionRepository situacionRepository;
    private final PacienteSituacionRepository pacienteSituacionRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;
    private final ConsentimientoService consentimientoService;
    private final DireccionRepository direccionRepository;
    private final TutorRepository tutorRepository;
    private final PsicologoRepository psicologoRepository;
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

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        Long idPsicologo = null;

        // 🔥 CLAVE: decidir qué devolver
        if (usuario.getRol() == RolUsuario.psicologo) {
            idPsicologo = psicologoRepository
                    .findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .map(Psicologo::getIdPsicologo)
                    .orElse(null);
        } else if (usuario.getRol() == RolUsuario.paciente) {
            // buscar el psicólogo asignado
            idPsicologo = psicologoPacienteRepository
                    .findByPaciente_Usuario_IdUsuario(usuario.getIdUsuario())
                    .map(rel -> rel.getPsicologo().getIdPsicologo())
                    .orElse(null);
        }

        Long idPaciente = null;

        if (usuario.getRol() == RolUsuario.paciente) {
            idPaciente = pacienteRepository
                    .findByUsuario_IdUsuario(usuario.getIdUsuario())
                    .map(Paciente::getIdPaciente)
                    .orElse(null);
        }

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,  // 👈 AQUÍ
                idPaciente   // 👈 AQUÍ
        );
    }

    // ================= REGISTER PACIENTE =================
    @Transactional
    public LoginResponseDTO registerPaciente(PacienteRequestDTO request) {

        // 0. Validaciones básicas
        if (request.getFechaNacimiento() == null) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria");
        }

        if (!Boolean.TRUE.equals(request.getAceptaTerminos())) {
            throw new RuntimeException("Debe aceptar los términos y condiciones");
        }

        // 1. Calcular si es menor
        int edad = Period.between(request.getFechaNacimiento(), LocalDate.now()).getYears();
        boolean esMenor = edad < 18;

        // 2. Validación de tutores
        if (esMenor && (request.getTutores() == null || request.getTutores().isEmpty())) {
            throw new RuntimeException("Un menor debe tener al menos un tutor");
        }

        // 3. Validar email único
        if (usuarioRepository.findByEmail(request.getUsuario().getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        LocalDateTime now = LocalDateTime.now();

        // --- Crear usuario ---
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getUsuario().getNombre());
        usuario.setDni(request.getUsuario().getDni());
        usuario.setApellido(request.getUsuario().getApellido());
        usuario.setEmail(request.getUsuario().getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getUsuario().getPassword()));
        usuario.setRol(RolUsuario.paciente);
        usuario.setActivo(true);
        usuario.setFechaRegistro(now);

        usuarioRepository.save(usuario);

        // --- Crear paciente ---
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setEstadoPago(
                request.getEstadoPago() != null ? request.getEstadoPago() : EstadoPago.PENDIENTE
        );
        paciente.setMetodoPago(
                request.getMetodoPago() != null ? request.getMetodoPago() : MetodoPago.PRESENCIAL
        );
        paciente.setCreatedAt(now);

        paciente = pacienteRepository.save(paciente);

        // --- Consentimiento ---
        consentimientoService.guardarConsentimiento(
                paciente,
                request.getAceptaTerminos(),
                request.getAceptaVideoconferencia(),
                request.getAceptaComunicacion()
        );

        // --- Situaciones ---
        if (request.getIdSituaciones() != null && !request.getIdSituaciones().isEmpty()) {
            Set<PacienteSituacion> listaPs = new HashSet<>();
            for (Long idSituacion : request.getIdSituaciones()) {
                Situacion situacion = situacionRepository.findById(idSituacion)
                        .orElseThrow(() -> new RuntimeException("Situación no encontrada: " + idSituacion));

                PacienteSituacion ps = new PacienteSituacion();
                ps.setPaciente(paciente);
                ps.setSituacion(situacion);
                ps.setFechaRegistro(now);

                pacienteSituacionRepository.save(ps);
                listaPs.add(ps);  // <-- agregar a la lista local
            }
            paciente.setPacienteSituaciones(listaPs);  // <-- actualizar la entidad Paciente
        }

        // --- Tutores (solo si es menor) ---
        if (esMenor) {
            for (TutorRequestDTO tutorDTO : request.getTutores()) {

                // Validación básica por tutor
                if (tutorDTO.getNombre() == null || tutorDTO.getTipo() == null) {
                    throw new RuntimeException("El tutor debe tener nombre y tipo");
                }

                Tutor tutor = new Tutor();
                tutor.setPaciente(paciente);
                tutor.setNombre(tutorDTO.getNombre());
                tutor.setTelefono(tutorDTO.getTelefono());
                tutor.setEmail(tutorDTO.getEmail());
                tutor.setDni(tutorDTO.getDni());
                tutor.setTipo(tutorDTO.getTipo());

                tutorRepository.save(tutor);
            }
        }
        // --- DIRECCIONES ---
        if (request.getDireccion() != null && !request.getDireccion().isEmpty()) {

            for (DireccionRequestDTO dirDTO : request.getDireccion()) {

                if (dirDTO.getCalle() == null || dirDTO.getCalle().isBlank()) {
                    throw new RuntimeException("La dirección debe tener calle");
                }

                Direccion direccion = new Direccion();
                direccion.setPaciente(paciente);
                direccion.setCalle(dirDTO.getCalle());
                direccion.setCiudad(dirDTO.getCiudad());
                direccion.setProvincia(dirDTO.getProvincia());
                direccion.setCodigoPostal(dirDTO.getCodigoPostal());
                direccion.setPais(dirDTO.getPais());

                direccionRepository.save(direccion);
            }
        }

        // --- JWT ---
        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        Long idPsicologo = psicologoPacienteRepository
                .findByPaciente_Usuario_IdUsuario(usuario.getIdUsuario())
                .map(rel -> rel.getPsicologo().getIdPsicologo())
                .orElse(null);

        Long idPaciente = null;

        if (usuario.getRol() == RolUsuario.paciente) {
            idPaciente = pacienteRepository
                    .findByUsuario_IdUsuario(usuario.getIdUsuario())
                    .map(Paciente::getIdPaciente)
                    .orElse(null);
        }
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,
                idPaciente
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
        Long idPsicologo = null;

        if (usuario.getRol() == RolUsuario.psicologo) {
            // si el usuario es psicólogo, devolvemos su propio ID
            idPsicologo = usuario.getIdUsuario();
        } else if (usuario.getRol() == RolUsuario.paciente) {
            // si es paciente, buscamos el psicólogo asignado
            idPsicologo = psicologoPacienteRepository
                    .findByPaciente_Usuario_IdUsuario(usuario.getIdUsuario())
                    .map(rel -> rel.getPsicologo().getIdPsicologo())
                    .orElse(null); // null si no hay psicólogo asignado
        }

        Long idPaciente = null;

        if (usuario.getRol() == RolUsuario.paciente) {
            idPaciente = pacienteRepository
                    .findByUsuario_IdUsuario(usuario.getIdUsuario())
                    .map(Paciente::getIdPaciente)
                    .orElse(null);
        }

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,   // 👈 AQUÍ
                idPaciente   // 👈 AQUÍ
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

    /**
     * public LoginResponseDTO registerPsicologo(RegistryRequestDTO request) {
     * <p>
     * Usuario usuario = new Usuario();
     * usuario.setNombre(request.getNombre());
     * usuario.setApellido(request.getApellido());
     * usuario.setEmail(request.getEmail());
     * usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
     * usuario.setRol(RolUsuario.psicologo);
     * usuario.setActivo(true);
     * usuarioRepository.save(usuario);
     * <p>
     * Psicologo psicologo = new Psicologo();
     * psicologo.setUsuario(usuario);
     * <p>
     * // opcional: otros campos
     * psicologo.setEspecialidad();
     * psicologo.setExperiencia(0);
     * <p>
     * psicologo = psicologoRepository.save(psicologo);
     * <p>
     * Long idPsicologo = psicologo.getIdPsicologo(); // ✅ ESTE es el bueno
     * UserDetails userDetails = new User(
     * usuario.getEmail(),
     * usuario.getPassword(),
     * List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
     * );
     * <p>
     * String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());
     * <p>
     * return new LoginResponseDTO(
     * usuario.getIdUsuario(),
     * usuario.getNombre(),
     * usuario.getRol().name(),
     * token,
     * idPsicologo   // 👈 AQUÍ
     * );
     * }
     **/

    // ================= BAJA =================
    public void darBajaPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findByUsuario_IdUsuario(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        paciente.getUsuario().setActivo(false);
        paciente.getUsuario().setFechaBaja(LocalDateTime.now());
        usuarioRepository.save(paciente.getUsuario());
    }

}