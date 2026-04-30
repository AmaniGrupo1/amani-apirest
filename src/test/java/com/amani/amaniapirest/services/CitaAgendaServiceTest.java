package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.amani.amaniapirest.enums.ModalidadCita;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.HorarioPsicologo;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Pago;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.TiposTerapia;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repositories.BloqueoAgendaRepository;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.HorarioPsicologoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.terapiaService.TerapiaRepository;
import com.amani.amaniapirest.services.notificacion.NotificationServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaAgendaServiceTest {

    private static final Long ID_PSICOLOGO = 31L;
    private static final Long ID_PACIENTE = 21L;
    private static final Long ID_TERAPIA = 7L;
    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private HorarioPsicologoRepository horarioRepository;

    @Mock
    private BloqueoAgendaRepository bloqueoRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private PacientesRepository pacienteRepository;

    @Mock
    private CitaService citaService;

    @Mock
    private TerapiaRepository terapiaRepository;

    @Mock
    private NotificationServices notificacionService;

    private CitaAgendaService service;
    private Psicologo psicologo;
    private Paciente paciente;
    private TiposTerapia terapia;

    @BeforeEach
    void setUp() {
        service = new CitaAgendaService(
                citaRepository,
                horarioRepository,
                bloqueoRepository,
                psicologoRepository,
                pacienteRepository,
                citaService,
                terapiaRepository,
                notificacionService
        );
        psicologo = psicologo(ID_PSICOLOGO, "Dra", "Psi");
        paciente = paciente(ID_PACIENTE, "Ana", "Lopez");
        terapia = new TiposTerapia(ID_TERAPIA, "TCC", 50, BigDecimal.valueOf(45), true);
    }

    @Test
    void crearCitaAllowsBackToBackAppointmentsAtExistingEndBoundary() {
        Cita existing = cita(90L, LocalDateTime.of(MONDAY, LocalTime.of(9, 0)), 60);
        CitaRequestDTO request = request(LocalDateTime.of(MONDAY, LocalTime.of(10, 0)), 50);

        givenCreateDependencies(List.of(existing), true);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> {
            Cita saved = invocation.getArgument(0);
            saved.setIdCita(100L);
            return saved;
        });

        AgendaItemDTO response = service.crearCita(request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getHoraInicio()).isEqualTo(LocalTime.of(10, 0));
        assertThat(response.getHoraFin()).isEqualTo(LocalTime.of(10, 50));
        assertThat(response.getEstado()).isEqualTo("pendiente");
        verify(notificacionService).enviarNotificacion(paciente.getUsuario(), "Nueva cita creada",
                "Tu cita ha sido registrada para el día 2026-05-04 a las 10:00");
        verify(notificacionService).enviarNotificacion(psicologo.getUsuario(), "Nueva cita asignada",
                "Se ha creado una nueva cita en tu agenda");
    }

    @Test
    void crearCitaRejectsOverlappingAppointmentAndDoesNotSave() {
        Cita existing = cita(90L, LocalDateTime.of(MONDAY, LocalTime.of(10, 30)), 60);
        CitaRequestDTO request = request(LocalDateTime.of(MONDAY, LocalTime.of(10, 0)), 50);

        givenCreateDependencies(List.of(existing), false);

        assertThatThrownBy(() -> service.crearCita(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ya existe una cita en ese horario");
        verify(citaRepository, never()).save(any());
        verify(notificacionService, never()).enviarNotificacion(any(), any(), any());
    }

    @Test
    void crearCitaRejectsMissingTherapyBeforeSaving() {
        CitaRequestDTO request = request(LocalDateTime.of(MONDAY, LocalTime.of(10, 0)), 50);
        request.setIdTipoTerapia(null);

        when(psicologoRepository.findById(ID_PSICOLOGO)).thenReturn(Optional.of(psicologo));
        when(pacienteRepository.findById(ID_PACIENTE)).thenReturn(Optional.of(paciente));
        when(horarioRepository.findByPsicologoIdPsicologoAndActivoTrue(ID_PSICOLOGO)).thenReturn(List.of(mondaySchedule()));
        when(bloqueoRepository.findByPsicologoIdPsicologoAndFecha(ID_PSICOLOGO, MONDAY)).thenReturn(List.of());
        when(citaRepository.findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
                ID_PSICOLOGO,
                MONDAY.atStartOfDay(),
                MONDAY.plusDays(1).atStartOfDay(),
                EstadoCita.cancelada
        )).thenReturn(List.of());

        assertThatThrownBy(() -> service.crearCita(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("idTipoTerapia es obligatorio");
        verify(citaRepository, never()).save(any());
    }

    @Test
    void cancelarCitaRefundsPaidAppointmentAndNotifiesBothUsers() {
        Cita cita = cita(100L, LocalDateTime.of(MONDAY, LocalTime.of(10, 0)), 50);
        Pago pago = new Pago();
        pago.setEstadoPago(EstadoPago.PAGADO);
        pago.setMetodoPago(MetodoPago.ONLINE);
        pago.setMonto(BigDecimal.valueOf(45));
        cita.setPago(pago);

        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendaItemDTO response = service.cancelarCita(100L);

        ArgumentCaptor<Cita> citaCaptor = ArgumentCaptor.forClass(Cita.class);
        verify(citaRepository).save(citaCaptor.capture());
        assertThat(citaCaptor.getValue().getEstado()).isEqualTo(EstadoCita.cancelada);
        assertThat(citaCaptor.getValue().getPago().getEstadoPago()).isEqualTo(EstadoPago.REEMBOLSADO);
        assertThat(response.getEstado()).isEqualTo("cancelada");
        verify(notificacionService).enviarNotificacion(paciente.getUsuario(), "Cita cancelada", "Tu cita ha sido cancelada");
        verify(notificacionService).enviarNotificacion(psicologo.getUsuario(), "Cita cancelada", "Una cita en tu agenda ha sido cancelada");
    }

    @Test
    void cancelarCitaRejectsAlreadyCancelledAppointment() {
        Cita cita = cita(100L, LocalDateTime.of(MONDAY, LocalTime.of(10, 0)), 50);
        cita.setEstado(EstadoCita.cancelada);

        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));

        assertThatThrownBy(() -> service.cancelarCita(100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La cita ya está cancelada");
        verify(citaRepository, never()).save(any());
        verify(notificacionService, never()).enviarNotificacion(any(), any(), any());
    }

    @Test
    void cancelarCitaRejectsUnknownAppointment() {
        when(citaRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelarCita(404L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Cita no encontrada");
    }

    private void givenCreateDependencies(List<Cita> existingAppointments, boolean includeTherapy) {
        when(psicologoRepository.findById(ID_PSICOLOGO)).thenReturn(Optional.of(psicologo));
        when(pacienteRepository.findById(ID_PACIENTE)).thenReturn(Optional.of(paciente));
        when(horarioRepository.findByPsicologoIdPsicologoAndActivoTrue(ID_PSICOLOGO)).thenReturn(List.of(mondaySchedule()));
        when(bloqueoRepository.findByPsicologoIdPsicologoAndFecha(ID_PSICOLOGO, MONDAY)).thenReturn(List.of());
        when(citaRepository.findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
                ID_PSICOLOGO,
                MONDAY.atStartOfDay(),
                MONDAY.plusDays(1).atStartOfDay(),
                EstadoCita.cancelada
        )).thenReturn(existingAppointments);
        if (includeTherapy) {
            when(terapiaRepository.findById(ID_TERAPIA)).thenReturn(Optional.of(terapia));
        }
    }

    private CitaRequestDTO request(LocalDateTime start, int durationMinutes) {
        CitaRequestDTO request = new CitaRequestDTO();
        request.setIdPaciente(ID_PACIENTE);
        request.setIdPsicologo(ID_PSICOLOGO);
        request.setStartDatetime(start);
        request.setDurationMinutes(durationMinutes);
        request.setEstado(EstadoCita.pendiente);
        request.setMetodoPago(MetodoPago.ONLINE);
        request.setEstadoPago(EstadoPago.PENDIENTE);
        request.setMonto(BigDecimal.valueOf(45));
        request.setMotivo("Seguimiento");
        request.setIdTipoTerapia(ID_TERAPIA);
        request.setModalidad(ModalidadCita.LLAMADA);
        return request;
    }

    private HorarioPsicologo mondaySchedule() {
        HorarioPsicologo horario = new HorarioPsicologo();
        horario.setPsicologo(psicologo);
        horario.setDiaSemana((short) 0);
        horario.setHoraInicio(LocalTime.of(9, 0));
        horario.setHoraFin(LocalTime.of(12, 0));
        horario.setActivo(true);
        return horario;
    }

    private Cita cita(Long id, LocalDateTime start, int durationMinutes) {
        Cita cita = new Cita();
        cita.setIdCita(id);
        cita.setPaciente(paciente);
        cita.setPsicologo(psicologo);
        cita.setStartDatetime(start);
        cita.setDurationMinutes(durationMinutes);
        cita.setEstado(EstadoCita.pendiente);
        cita.setModalidad(ModalidadCita.LLAMADA);
        cita.setMotivo("Seguimiento");
        cita.setTipoTerapia(terapia);
        return cita;
    }

    private Paciente paciente(Long id, String nombre, String apellido) {
        Paciente paciente = new Paciente();
        paciente.setIdPaciente(id);
        paciente.setUsuario(usuario(id + 1_000L, nombre, apellido));
        return paciente;
    }

    private Psicologo psicologo(Long id, String nombre, String apellido) {
        Psicologo psicologo = new Psicologo();
        psicologo.setIdPsicologo(id);
        psicologo.setUsuario(usuario(id + 1_000L, nombre, apellido));
        return psicologo;
    }

    private Usuario usuario(Long id, String nombre, String apellido) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        return usuario;
    }
}
