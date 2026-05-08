package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.profile.psicologo.PsicologoDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.mappers.ProfileMapper;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PsicologoSelfServiceTest {

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PacientesRepository pacientesRepository;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PsicologoSelfService service;

    private Psicologo buildPsicologo(Long id, String nombre, String apellido) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id + 1000L);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(nombre.toLowerCase() + "@amani.com");

        Psicologo psicologo = new Psicologo();
        psicologo.setIdPsicologo(id);
        psicologo.setUsuario(usuario);
        psicologo.setEspecialidad("Ansiedad");
        psicologo.setExperiencia(5);
        psicologo.setDescripcion("Especialista en TCC");
        psicologo.setLicencia("LIC-123");
        return psicologo;
    }

    private PsicologoRequestDTO buildRequest() {
        return new PsicologoRequestDTO(
                "Carlos",
                "García",
                "carlos@amani.com",
                "password123",
                "Depresión",
                10,
                "Experto en DBT",
                "LIC-456"
        );
    }

    @Test
    void findAllConDatos() {
        Psicologo p1 = buildPsicologo(1L, "Ana", "López");
        Psicologo p2 = buildPsicologo(2L, "Luis", "Ruiz");
        when(psicologoRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PsicologoSelfResponseDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIdPsicologo()).isEqualTo(1L);
        assertThat(result.get(0).getNombre()).isEqualTo("Ana");
        assertThat(result.get(1).getIdPsicologo()).isEqualTo(2L);
    }

    @Test
    void findAllVacio() {
        when(psicologoRepository.findAll()).thenReturn(List.of());

        List<PsicologoSelfResponseDTO> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findByIdExiste() {
        Psicologo psicologo = buildPsicologo(1L, "Ana", "López");
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));

        PsicologoSelfResponseDTO result = service.findById(1L);

        assertThat(result.getIdPsicologo()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Ana");
    }

    @Test
    void findByIdNoExiste() {
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(404L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    void findProfileByIdExiste() {
        Psicologo psicologo = buildPsicologo(1L, "Ana", "López");
        PsicologoDTO expectedDto = new PsicologoDTO();
        expectedDto.setIdPsicologo(1L);
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));
        when(profileMapper.toPsicologoDTO(psicologo)).thenReturn(expectedDto);

        PsicologoDTO result = service.findProfileById(1L);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void findProfileByIdNoExiste() {
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findProfileById(404L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    void createExitoso() {
        PsicologoRequestDTO request = buildRequest();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        Usuario savedUsuario = new Usuario();
        savedUsuario.setIdUsuario(100L);
        savedUsuario.setNombre(request.getNombrePsicologo());
        savedUsuario.setApellido(request.getApellidoPsicologo());
        savedUsuario.setEmail(request.getEmail());
        savedUsuario.setPassword("encodedPass");
        savedUsuario.setRol(RolUsuario.psicologo);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        Psicologo savedPsicologo = new Psicologo();
        savedPsicologo.setIdPsicologo(1L);
        savedPsicologo.setUsuario(savedUsuario);
        savedPsicologo.setEspecialidad(request.getEspecialidad());
        savedPsicologo.setExperiencia(request.getExperiencia());
        savedPsicologo.setDescripcion(request.getDescripcion());
        savedPsicologo.setLicencia(request.getLicencia());
        when(psicologoRepository.save(any(Psicologo.class))).thenReturn(savedPsicologo);

        PsicologoSelfResponseDTO result = service.create(request);

        assertThat(result.getIdPsicologo()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Carlos");
        assertThat(result.getEspecialidad()).isEqualTo("Depresión");
        verify(usuarioRepository).existsByEmail(request.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(psicologoRepository).save(any(Psicologo.class));
    }

    @Test
    void createEmailDuplicado() {
        PsicologoRequestDTO request = buildRequest();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    void updateExitoso() {
        Psicologo psicologo = buildPsicologo(1L, "Ana", "López");
        PsicologoRequestDTO request = new PsicologoRequestDTO(
                "Ana María",
                "López",
                "ana.lopez@amani.com",
                null,
                "Trauma",
                8,
                "Descripción actualizada",
                "LIC-789"
        );
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(psicologoRepository.save(any(Psicologo.class))).thenAnswer(inv -> inv.getArgument(0));

        PsicologoSelfResponseDTO result = service.update(1L, request);

        assertThat(result.getNombre()).isEqualTo("Ana María");
        assertThat(result.getEspecialidad()).isEqualTo("Trauma");
        assertThat(result.getLicencia()).isEqualTo("LIC-789");
    }

    @Test
    void updatePsicologoNoExiste() {
        PsicologoRequestDTO request = buildRequest();
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(404L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    void updateCambioEmailADuplicado() {
        Psicologo psicologo = buildPsicologo(1L, "Ana", "López");
        PsicologoRequestDTO request = new PsicologoRequestDTO(
                "Ana",
                "López",
                "duplicado@amani.com",
                null,
                "Ansiedad",
                5,
                "Desc",
                "LIC-123"
        );
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    void deleteExitoso() {
        Psicologo psicologo = buildPsicologo(1L, "Ana", "López");
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));

        service.delete(1L);

        verify(psicologoRepository).delete(psicologo);
    }

    @Test
    void deleteNoExiste() {
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(404L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }
}
