package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.UsuarioRequestDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacienteSituacionRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.SituacionRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteAdminServiceTest {

    @Mock
    private PacientesRepository pacientesRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private PsicologoPacienteRepository psicologoPacienteRepository;

    @Mock
    private SituacionRepository situacionRepository;

    @Mock
    private PacienteSituacionRepository pacienteSituacionRepository;

    @InjectMocks
    private PacienteAdminService service;

    private Usuario buildUsuario(Long id, String nombre, String apellido) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(nombre.toLowerCase() + "@amani.com");
        usuario.setDni("12345678A");
        usuario.setRol(RolUsuario.paciente);
        usuario.setActivo(true);
        return usuario;
    }

    private Paciente buildPaciente(Long id, String nombre, String apellido) {
        Paciente paciente = new Paciente();
        paciente.setIdPaciente(id);
        paciente.setUsuario(buildUsuario(id + 1000L, nombre, apellido));
        paciente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        paciente.setGenero("masculino");
        paciente.setTelefono("600000000");
        return paciente;
    }

    private Psicologo buildPsicologo(Long id, String nombre, String apellido) {
        Psicologo psicologo = new Psicologo();
        psicologo.setIdPsicologo(id);
        psicologo.setUsuario(buildUsuario(id + 2000L, nombre, apellido));
        psicologo.setEspecialidad("Ansiedad");
        psicologo.setLicencia("LIC-" + id);
        return psicologo;
    }

    @Test
    void findAllConDatos() {
        Paciente p1 = buildPaciente(1L, "Carlos", "López");
        Paciente p2 = buildPaciente(2L, "Ana", "García");
        when(pacientesRepository.findAllWithSituacionesTutoresYDirecciones()).thenReturn(List.of(p1, p2));

        List<PacienteAdminResponseDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIdPaciente()).isEqualTo(1L);
        assertThat(result.get(1).getIdPaciente()).isEqualTo(2L);
    }

    @Test
    void findByIdExiste() {
        Paciente paciente = buildPaciente(1L, "Carlos", "López");
        when(pacientesRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteAdminResponseDTO result = service.findById(1L);

        assertThat(result.getIdPaciente()).isEqualTo(1L);
        assertThat(result.getNombreUsuario()).isEqualTo("Carlos");
    }

    @Test
    void findByIdNoExiste() {
        when(pacientesRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Paciente no encontrado");
    }

    @Test
    void asignarPsicologoExitoso() {
        Paciente paciente = buildPaciente(1L, "Carlos", "López");
        Psicologo psicologo = buildPsicologo(10L, "Dra", "Psi");
        when(pacientesRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(psicologoRepository.findById(10L)).thenReturn(Optional.of(psicologo));
        when(psicologoPacienteRepository.findByPacienteIdPacienteAndFechaFinIsNull(1L)).thenReturn(Optional.empty());
        when(psicologoPacienteRepository.save(any(PsicologoPaciente.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pacientesRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean result = service.asignarPsicologo(1L, 10L);

        assertThat(result).isTrue();
        verify(psicologoPacienteRepository).save(any(PsicologoPaciente.class));
        verify(pacientesRepository).save(paciente);
    }

    @Test
    void asignarPsicologoPacienteNoExiste() {
        when(pacientesRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.asignarPsicologo(404L, 10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Paciente no encontrado");
    }

    @Test
    void asignarPsicologoPsicologoNoExiste() {
        Paciente paciente = buildPaciente(1L, "Carlos", "López");
        when(pacientesRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.asignarPsicologo(1L, 404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Psicólogo no encontrado");
    }

    @Test
    void createExitoso() {
        UsuarioRequestDTO usuarioRequest = new UsuarioRequestDTO(
                null,
                "Carlos",
                "12345678A",
                "López",
                "carlos@amani.com",
                "password123",
                RolUsuario.paciente,
                true
        );
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setUsuario(usuarioRequest);
        request.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        request.setGenero("masculino");
        request.setTelefono("600000000");
        request.setAceptaTerminos(true);

        Usuario savedUsuario = buildUsuario(100L, "Carlos", "López");
        savedUsuario.setEmail("carlos@amani.com");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        Paciente savedPaciente = buildPaciente(1L, "Carlos", "López");
        savedPaciente.setUsuario(savedUsuario);
        when(pacientesRepository.save(any(Paciente.class))).thenReturn(savedPaciente);

        PacienteAdminResponseDTO result = service.create(request);

        assertThat(result.getIdPaciente()).isEqualTo(1L);
        assertThat(result.getNombreUsuario()).isEqualTo("Carlos");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(pacientesRepository).save(any(Paciente.class));
    }

    @Test
    void updateExitoso() {
        Paciente paciente = buildPaciente(1L, "Carlos", "López");
        Usuario usuario = buildUsuario(200L, "Carlos", "López");
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setIdUsuario(200L);
        request.setFechaNacimiento(LocalDate.of(1991, 2, 2));
        request.setGenero("femenino");
        request.setTelefono("611111111");
        request.setAceptaTerminos(true);

        when(pacientesRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuario));
        when(pacientesRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        PacienteAdminResponseDTO result = service.update(1L, request);

        assertThat(result.getIdPaciente()).isEqualTo(1L);
        assertThat(result.getGenero()).isEqualTo("femenino");
        assertThat(result.getTelefono()).isEqualTo("611111111");
    }

    @Test
    void updateNoExiste() {
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setIdUsuario(200L);
        request.setAceptaTerminos(true);
        when(pacientesRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(404L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Paciente no encontrado");
    }
}
