package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.PacientesAsignadoDTO;
import com.amani.amaniapirest.dto.loginDTO.PsicologoConPacientesDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.PsicologoPaciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PsicologoAdminServiceTest {

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private PsicologoPacienteRepository psicologoPacienteRepository;

    @InjectMocks
    private PsicologoAdminService service;

    private Usuario buildUsuario(Long id, String nombre, String apellido, String email) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setActivo(true);
        return usuario;
    }

    private Psicologo buildPsicologo(Long id, String nombre, String apellido) {
        Psicologo psicologo = new Psicologo();
        psicologo.setIdPsicologo(id);
        psicologo.setUsuario(buildUsuario(id + 1000L, nombre, apellido, nombre.toLowerCase() + "@amani.com"));
        psicologo.setEspecialidad("Ansiedad");
        psicologo.setExperiencia(5);
        psicologo.setDescripcion("Descripción");
        psicologo.setLicencia("LIC-" + id);
        return psicologo;
    }

    private PsicologoPaciente buildPsicologoPaciente(Paciente paciente, Psicologo psicologo) {
        PsicologoPaciente pp = new PsicologoPaciente();
        pp.setId(1L);
        pp.setPaciente(paciente);
        pp.setPsicologo(psicologo);
        return pp;
    }

    private Paciente buildPaciente(Long id, String nombre, String apellido) {
        Paciente paciente = new Paciente();
        paciente.setIdPaciente(id);
        paciente.setUsuario(buildUsuario(id + 2000L, nombre, apellido, nombre.toLowerCase() + "@amani.com"));
        return paciente;
    }

    @Test
    void updateExitoso() {
        Psicologo psicologo = buildPsicologo(1L, "Dra", "Psi");
        PsicologoRequestDTO request = new PsicologoRequestDTO();
        request.setEspecialidad("Trauma");
        request.setExperiencia(10);
        request.setDescripcion("Nueva descripción");
        request.setLicencia("LIC-NEW");

        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));
        when(psicologoRepository.save(any(Psicologo.class))).thenAnswer(inv -> inv.getArgument(0));

        PsicologoConPacientesDTO result = service.update(1L, request);

        assertThat(result.getIdPsicologo()).isEqualTo(1L);
        assertThat(result.getEspecialidad()).isEqualTo("Trauma");
        assertThat(result.getLicencia()).isEqualTo("LIC-NEW");
        verify(psicologoRepository).save(psicologo);
    }

    @Test
    void updateNoExiste() {
        PsicologoRequestDTO request = new PsicologoRequestDTO();
        request.setEspecialidad("Trauma");
        request.setLicencia("LIC-999");
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(404L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Psicólogo no encontrado");
    }

    @Test
    void deleteExitoso() {
        Psicologo psicologo = buildPsicologo(1L, "Dra", "Psi");
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psicologo));

        service.delete(1L);

        verify(psicologoRepository).delete(psicologo);
    }

    @Test
    void deleteNoExiste() {
        when(psicologoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Psicólogo no encontrado");
    }

    @Test
    void getPsicologosConPacientesConDatos() {
        Psicologo p1 = buildPsicologo(1L, "Dra", "Uno");
        Psicologo p2 = buildPsicologo(2L, "Dr", "Dos");
        Paciente pac1 = buildPaciente(10L, "Ana", "Pac");
        Paciente pac2 = buildPaciente(11L, "Luis", "Pac");

        PsicologoPaciente pp1 = buildPsicologoPaciente(pac1, p1);
        PsicologoPaciente pp2 = buildPsicologoPaciente(pac2, p2);

        when(psicologoRepository.findByUsuario_ActivoTrue()).thenReturn(List.of(p1, p2));
        when(psicologoPacienteRepository.findByPsicologoIdPsicologoAndFechaFinIsNull(1L)).thenReturn(List.of(pp1));
        when(psicologoPacienteRepository.findByPsicologoIdPsicologoAndFechaFinIsNull(2L)).thenReturn(List.of(pp2));

        List<PsicologoConPacientesDTO> result = service.getPsicologosConPacientes();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIdPsicologo()).isEqualTo(1L);
        assertThat(result.get(0).getPacientes()).hasSize(1);
        assertThat(result.get(0).getPacientes().get(0).getIdPaciente()).isEqualTo(10L);
        assertThat(result.get(1).getPacientes()).hasSize(1);
    }

    @Test
    void getPsicologosConPacientesVacio() {
        when(psicologoRepository.findByUsuario_ActivoTrue()).thenReturn(List.of());

        List<PsicologoConPacientesDTO> result = service.getPsicologosConPacientes();

        assertThat(result).isEmpty();
    }
}
