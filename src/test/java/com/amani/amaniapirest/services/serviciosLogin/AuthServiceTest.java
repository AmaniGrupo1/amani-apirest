package com.amani.amaniapirest.services.serviciosLogin;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.TutorRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.UsuarioRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.DireccionRepository;
import com.amani.amaniapirest.repository.PacienteSituacionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.SituacionRepository;
import com.amani.amaniapirest.repository.TutorRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import com.amani.amaniapirest.services.ConsentimientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PacientesRepository pacienteRepository;

    @Mock
    private SituacionRepository situacionRepository;

    @Mock
    private PacienteSituacionRepository pacienteSituacionRepository;

    @Mock
    private PsicologoPacienteRepository psicologoPacienteRepository;

    @Mock
    private ConsentimientoService consentimientoService;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private SecurityConfig securityConfig;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(
                usuarioRepository,
                pacienteRepository,
                situacionRepository,
                pacienteSituacionRepository,
                psicologoPacienteRepository,
                consentimientoService,
                direccionRepository,
                tutorRepository,
                psicologoRepository,
                securityConfig,
                jwtUtil
        );
    }

    @Test
    void loginPacienteReturnsPacienteIdAndAssignedPsicologoId() {
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
        Usuario usuario = usuario(11L, "Ana", "ana@amani.com", RolUsuario.paciente);
        usuario.setPassword(passwordEncoder.encode("secret"));
        Paciente paciente = paciente(21L, usuario);
        Psicologo psicologo = psicologo(31L, usuario(12L, "Psi", "psi@amani.com", RolUsuario.psicologo));
        PsicologoPaciente relacion = new PsicologoPaciente();
        relacion.setPaciente(paciente);
        relacion.setPsicologo(psicologo);
        LoginRequestDTO request = loginRequest("ana@amani.com", "secret");

        when(usuarioRepository.findByEmail("ana@amani.com")).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByUsuario_IdUsuario(11L)).thenReturn(Optional.of(paciente));
        when(psicologoPacienteRepository.findByPaciente_Usuario_IdUsuario(11L)).thenReturn(Optional.of(relacion));
        when(jwtUtil.generateToken(any(), org.mockito.ArgumentMatchers.eq("paciente"))).thenReturn("jwt-token");

        LoginResponseDTO response = authService.login(request);

        assertThat(response.getIdUsuario()).isEqualTo(11L);
        assertThat(response.getRol()).isEqualTo("paciente");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getIdPaciente()).isEqualTo(21L);
        assertThat(response.getIdPsicologo()).isEqualTo(31L);
    }

    @Test
    void loginRejectsWrongPasswordWithoutGeneratingToken() {
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
        Usuario usuario = usuario(11L, "Ana", "ana@amani.com", RolUsuario.paciente);
        usuario.setPassword(passwordEncoder.encode("secret"));

        when(usuarioRepository.findByEmail("ana@amani.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(loginRequest("ana@amani.com", "wrong")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Contraseña incorrecta");
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void registerPacienteRejectsMinorWithoutTutor() {
        PacienteRequestDTO request = validAdultRequest();
        request.setFechaNacimiento(LocalDate.now().minusYears(12));
        request.setTutores(List.of());

        assertThatThrownBy(() -> authService.registerPaciente(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Un menor debe tener al menos un tutor");
        verify(usuarioRepository, never()).save(any());
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void registerPacientePersistsEncodedPasswordConsentSituationsTutorsAndAddress() {
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
        PacienteRequestDTO request = validAdultRequest();
        request.setFechaNacimiento(LocalDate.now().minusYears(15));
        request.setTutores(List.of(new TutorRequestDTO("Tutor Legal", "600000000", "tutor@amani.com", "99999999T", "TUTOR")));
        request.setIdSituaciones(List.of(5L));
        request.setDireccion(List.of(new DireccionRequestDTO(null, "Calle Salud", "Madrid", "Madrid", "28001", "España")));
        Situacion situacion = new Situacion();
        situacion.setIdSituacion(5L);

        when(usuarioRepository.findByEmail("ana@amani.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setIdUsuario(101L);
            return saved;
        });
        when(pacienteRepository.save(any())).thenAnswer(invocation -> {
            Paciente saved = invocation.getArgument(0);
            saved.setIdPaciente(201L);
            return saved;
        });
        when(situacionRepository.findById(5L)).thenReturn(Optional.of(situacion));
        when(psicologoPacienteRepository.findByPaciente_Usuario_IdUsuario(101L)).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(any(), org.mockito.ArgumentMatchers.eq("paciente"))).thenReturn("new-token");

        LoginResponseDTO response = authService.registerPaciente(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getRol()).isEqualTo(RolUsuario.paciente);
        assertThat(savedUsuario.getActivo()).isTrue();
        assertThat(savedUsuario.getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", savedUsuario.getPassword())).isTrue();

        verify(consentimientoService).guardarConsentimiento(
                any(Paciente.class),
                org.mockito.ArgumentMatchers.eq(true),
                org.mockito.ArgumentMatchers.eq(true),
                org.mockito.ArgumentMatchers.eq(false)
        );
        verify(pacienteSituacionRepository).save(any());
        verify(tutorRepository).save(any());
        verify(direccionRepository).save(any());
        assertThat(response.getIdUsuario()).isEqualTo(101L);
        assertThat(response.getIdPaciente()).isEqualTo(101L);
        assertThat(response.getToken()).isEqualTo("new-token");
    }

    private LoginRequestDTO loginRequest(String email, String password) {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private PacienteRequestDTO validAdultRequest() {
        UsuarioRequestDTO usuario = new UsuarioRequestDTO();
        usuario.setNombre("Ana");
        usuario.setApellido("Lopez");
        usuario.setDni("12345678A");
        usuario.setEmail("ana@amani.com");
        usuario.setPassword("secret123");

        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setUsuario(usuario);
        request.setFechaNacimiento(LocalDate.now().minusYears(25));
        request.setGenero("femenino");
        request.setTelefono("600000001");
        request.setAceptaTerminos(true);
        request.setAceptaVideoconferencia(true);
        request.setAceptaComunicacion(false);
        return request;
    }

    private Usuario usuario(Long id, String nombre, String email, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuario;
    }

    private Paciente paciente(Long id, Usuario usuario) {
        Paciente paciente = new Paciente();
        paciente.setIdPaciente(id);
        paciente.setUsuario(usuario);
        return paciente;
    }

    private Psicologo psicologo(Long id, Usuario usuario) {
        Psicologo psicologo = new Psicologo();
        psicologo.setIdPsicologo(id);
        psicologo.setUsuario(usuario);
        return psicologo;
    }
}
