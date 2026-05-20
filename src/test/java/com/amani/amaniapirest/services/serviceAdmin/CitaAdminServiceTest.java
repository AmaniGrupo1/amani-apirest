package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.response.CitaAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaAdminServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private PacientesRepository pacientesRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @InjectMocks
    private CitaAdminService service;

    private Usuario buildUsuario(String nombre, String apellido) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        return usuario;
    }

    private Paciente buildPaciente(Long id, String nombre, String apellido) {
        Paciente paciente = new Paciente();
        paciente.setIdPaciente(id);
        paciente.setUsuario(buildUsuario(nombre, apellido));
        return paciente;
    }

    private Psicologo buildPsicologo(Long id, String nombre, String apellido) {
        Psicologo psicologo = new Psicologo();
        psicologo.setIdPsicologo(id);
        psicologo.setUsuario(buildUsuario(nombre, apellido));
        return psicologo;
    }

    private Cita buildCita(Long id, Paciente p, Psicologo psi) {
        Cita cita = new Cita();
        cita.setIdCita(id);
        cita.setPaciente(p);
        cita.setPsicologo(psi);
        cita.setStartDatetime(LocalDateTime.now().plusDays(1));
        cita.setDurationMinutes(60);
        cita.setEstado(EstadoCita.pendiente);
        cita.setMotivo("Consulta");
        return cita;
    }

    @Test
    void findAllAdminConDatos() {
        Paciente p = buildPaciente(1L, "Carlos", "López");
        Psicologo psi = buildPsicologo(1L, "Dra", "Psi");
        Cita c1 = buildCita(10L, p, psi);
        
        when(citaRepository.findAll()).thenReturn(List.of(c1));

        List<CitaAdminResponseDTO> result = service.findAllAdmin();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombrePaciente()).isEqualTo("Carlos");
        assertThat(result.get(0).getNombrePsicologo()).isEqualTo("Dra");
    }

    @Test
    void findByIdAdminExiste() {
        Paciente p = buildPaciente(1L, "Carlos", "López");
        Psicologo psi = buildPsicologo(1L, "Dra", "Psi");
        Cita c1 = buildCita(10L, p, psi);

        when(citaRepository.findById(10L)).thenReturn(Optional.of(c1));

        CitaAdminResponseDTO result = service.findByIdAdmin(10L);

        assertThat(result.getNombrePaciente()).isEqualTo("Carlos");
    }

    @Test
    void findByIdAdminNoExiste() {
        when(citaRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByIdAdmin(404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cita no encontrada");
    }

    @Test
    void createAdminExitoso() {
        Paciente p = buildPaciente(1L, "Carlos", "López");
        Psicologo psi = buildPsicologo(1L, "Dra", "Psi");
        CitaRequestDTO request = new CitaRequestDTO();
        request.setIdPaciente(1L);
        request.setIdPsicologo(1L);
        request.setStartDatetime(LocalDateTime.now().plusDays(1));
        request.setDurationMinutes(60);
        request.setEstado(EstadoCita.confirmada);
        request.setMotivo("Motivo");

        when(pacientesRepository.findById(1L)).thenReturn(Optional.of(p));
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psi));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> {
            Cita c = inv.getArgument(0);
            c.setIdCita(100L);
            return c;
        });

        CitaAdminResponseDTO result = service.createAdmin(request);

        assertThat(result.getNombrePaciente()).isEqualTo("Carlos");
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    void updateAdminExitoso() {
        Paciente p = buildPaciente(1L, "Carlos", "López");
        Psicologo psi = buildPsicologo(1L, "Dra", "Psi");
        Cita cita = buildCita(10L, p, psi);
        
        CitaRequestDTO request = new CitaRequestDTO();
        request.setIdPaciente(1L);
        request.setIdPsicologo(1L);
        request.setStartDatetime(LocalDateTime.now().plusDays(2));
        request.setEstado(EstadoCita.confirmada);

        when(citaRepository.findById(10L)).thenReturn(Optional.of(cita));
        when(pacientesRepository.findById(1L)).thenReturn(Optional.of(p));
        when(psicologoRepository.findById(1L)).thenReturn(Optional.of(psi));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        CitaAdminResponseDTO result = service.updateAdmin(10L, request);

        assertThat(result.getEstadoCita()).isEqualTo("confirmada");
        verify(citaRepository).save(cita);
    }
}
