package com.amani.amaniapirest.services;

import com.amani.amaniapirest.models.BloqueoAgenda;
import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.HorarioPsicologo;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repositories.BloqueoAgendaRepository;
import com.amani.amaniapirest.repository.CitaRepository;
import com.amani.amaniapirest.repository.HorarioPsicologoRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.dto.dtoAgenda.request.BloqueoRequestDTO;
import com.amani.amaniapirest.dto.dtoAgenda.request.CrearCitaRequestDTO;
import com.amani.amaniapirest.dto.dtoAgenda.request.HorarioRequestDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.DisponibilidadDTO;
import com.amani.amaniapirest.dto.dtoAgenda.response.SlotDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CitaAgendaService {

    private static final int SLOT_MINUTOS = 50;

    private final CitaRepository citaRepository;
    private final HorarioPsicologoRepository horarioRepository;
    private final BloqueoAgendaRepository bloqueoRepository;
    private final PsicologoRepository psicologoRepository;
    private final PacientesRepository pacienteRepository;

    public CitaAgendaService(
            CitaRepository citaRepository,
            HorarioPsicologoRepository horarioRepository,
            BloqueoAgendaRepository bloqueoRepository,
            PsicologoRepository psicologoRepository,
            PacientesRepository pacienteRepository
    ) {
        this.citaRepository = citaRepository;
        this.horarioRepository = horarioRepository;
        this.bloqueoRepository = bloqueoRepository;
        this.psicologoRepository = psicologoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/citas/paciente/{id}/agenda?month=YYYY-MM
    // ─────────────────────────────────────────────────────────
    public List<AgendaItemDTO> getAgendaPaciente(Long idPaciente, String month) {
        var rango = rangoMes(month);
        return citaRepository
                .findByPaciente_IdPacienteAndStartDatetimeBetween(idPaciente, rango[0], rango[1])
                .stream()
                .map(this::citaToAgendaItem)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/citas/psicologo/{id}/agenda?month=YYYY-MM
    // ─────────────────────────────────────────────────────────
    public List<AgendaItemDTO> getAgendaPsicologo(Long idPsicologo, String month) {
        var rango = rangoMes(month);

        List<AgendaItemDTO> items = new ArrayList<>();

        // Citas del mes
        citaRepository.findByPsicologo_IdPsicologoAndStartDatetimeBetween(idPsicologo, rango[0], rango[1])
                .stream()
                .map(this::citaToAgendaItem)
                .forEach(items::add);

        // Bloqueos del mes
        LocalDate inicio = rango[0].toLocalDate();
        LocalDate fin    = rango[1].toLocalDate();
        bloqueoRepository
                .findByPsicologoIdPsicologoAndFechaBetween(idPsicologo, inicio, fin)
                .stream()
                .map(this::bloqueoToAgendaItem)
                .forEach(items::add);

        items.sort(Comparator.comparing(AgendaItemDTO::getFecha)
                .thenComparing(AgendaItemDTO::getHoraInicio));
        return items;
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/citas/psicologo/{id}/disponibilidad?fecha=YYYY-MM-DD
    // ─────────────────────────────────────────────────────────
    public DisponibilidadDTO getDisponibilidad(Long idPsicologo, String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);

        // ¿Día completo bloqueado?
        boolean diaCompleto = bloqueoRepository
                .findByPsicologoIdPsicologoAndFechaAndHoraInicioIsNull(idPsicologo, fecha)
                .isPresent();

        if (diaCompleto) {
            return DisponibilidadDTO.builder()
                    .fecha(fecha)
                    .diaCompleto(true)
                    .slotsLibres(Collections.emptyList())
                    .build();
        }

        // Horario del día de semana (0=lunes)
        short diaSemana = (short) (fecha.getDayOfWeek().getValue() - 1);
        List<HorarioPsicologo> franjas = horarioRepository
                .findByPsicologoIdPsicologoAndActivoTrue(idPsicologo)
                .stream()
                .filter(h -> h.getDiaSemana() == diaSemana)
                .toList();

        if (franjas.isEmpty()) {
            return DisponibilidadDTO.builder()
                    .fecha(fecha)
                    .diaCompleto(false)
                    .slotsLibres(Collections.emptyList())
                    .build();
        }

        // Generar todos los slots posibles
        Set<LocalTime> ocupados = new HashSet<>();

        // Slots ya citados
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();
        citaRepository.findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
                idPsicologo, inicioDia, finDia, EstadoCita.cancelada
        )
                .forEach(c -> ocupados.add(c.getStartDatetime().toLocalTime()));

        // Franjas parcialmente bloqueadas
        bloqueoRepository.findByPsicologoIdPsicologoAndFecha(idPsicologo, fecha)
                .stream()
                .filter(b -> b.getHoraInicio() != null)
                .forEach(b -> {
                    LocalTime t = b.getHoraInicio();
                    while (t.isBefore(b.getHoraFin())) {
                        ocupados.add(t);
                        t = t.plusMinutes(SLOT_MINUTOS);
                    }
                });

        List<SlotDTO> libres = new ArrayList<>();
        for (HorarioPsicologo franja : franjas) {
            LocalTime t = franja.getHoraInicio();
            while (!t.plusMinutes(SLOT_MINUTOS).isAfter(franja.getHoraFin())) {
                if (!ocupados.contains(t)) {
                    libres.add(SlotDTO.builder()
                            .horaInicio(t)
                            .horaFin(t.plusMinutes(SLOT_MINUTOS))
                            .build());
                }
                t = t.plusMinutes(SLOT_MINUTOS);
            }
        }

        return DisponibilidadDTO.builder()
                .fecha(fecha)
                .diaCompleto(false)
                .slotsLibres(libres)
                .build();
    }

    // ─────────────────────────────────────────────────────────
    // POST /api/citas
    // ─────────────────────────────────────────────────────────
    @Transactional
    public AgendaItemDTO crearCita(CrearCitaRequestDTO req) {
        LocalDateTime start = LocalDateTime.parse(req.getStartDatetime());

        // Validar disponibilidad
        DisponibilidadDTO disp = getDisponibilidad(
                req.getIdPsicologo(), start.toLocalDate().toString());

        boolean slotLibre = !disp.isDiaCompleto() && disp.getSlotsLibres().stream()
                .anyMatch(s -> s.getHoraInicio().equals(start.toLocalTime()));

        if (!slotLibre) {
            throw new IllegalStateException("El horario solicitado no está disponible");
        }

        Psicologo psicologo = psicologoRepository.findById(req.getIdPsicologo())
                .orElseThrow(() -> new NoSuchElementException("Psicólogo no encontrado"));
        Paciente paciente = pacienteRepository.findById(req.getIdPaciente())
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

        Cita cita = new Cita();
        cita.setPsicologo(psicologo);
        cita.setPaciente(paciente);
        cita.setStartDatetime(start);
        cita.setDurationMinutes(req.getDuracionMinutos() != null ? req.getDuracionMinutos() : SLOT_MINUTOS);
        cita.setEstado(EstadoCita.pendiente);
        cita.setMotivo(req.getMotivo());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());

        return citaToAgendaItem(citaRepository.save(cita));
    }

    // ─────────────────────────────────────────────────────────
    // PATCH /api/citas/{id}/cancelar
    // ─────────────────────────────────────────────────────────
    @Transactional
    public AgendaItemDTO cancelarCita(Long idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));

        if (cita.getEstado() == EstadoCita.completada) {
            throw new IllegalStateException("No se puede cancelar una cita completada");
        }

        cita.setEstado(EstadoCita.cancelada);
        return citaToAgendaItem(citaRepository.save(cita));
    }

    // ─────────────────────────────────────────────────────────
    // PUT /api/citas/psicologo/{id}/horario
    //   Reemplaza todo el horario semanal del psicólogo
    // ─────────────────────────────────────────────────────────
    @Transactional
    public void actualizarHorario(Long idPsicologo, HorarioRequestDTO req) {
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new NoSuchElementException("Psicólogo no encontrado"));

        horarioRepository.deleteByPsicologoIdPsicologo(idPsicologo);

        List<HorarioPsicologo> nuevas = req.getFranjas().stream()
                .map(f -> HorarioPsicologo.builder()
                        .psicologo(psicologo)
                        .diaSemana(f.getDiaSemana())
                        .horaInicio(LocalTime.parse(f.getHoraInicio()))
                        .horaFin(LocalTime.parse(f.getHoraFin()))
                        .activo(f.isActivo())
                        .build())
                .collect(Collectors.toList());

        horarioRepository.saveAll(nuevas);
    }

    // ─────────────────────────────────────────────────────────
    // POST /api/citas/psicologo/{id}/dias-no-disponibles
    // ─────────────────────────────────────────────────────────
    @Transactional
    public void addBloqueo(Long idPsicologo, BloqueoRequestDTO req) {
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new NoSuchElementException("Psicólogo no encontrado"));

        BloqueoAgenda bloqueo = BloqueoAgenda.builder()
                .psicologo(psicologo)
                .fecha(LocalDate.parse(req.getFecha()))
                .horaInicio(req.getHoraInicio() != null ? LocalTime.parse(req.getHoraInicio()) : null)
                .horaFin(req.getHoraFin()       != null ? LocalTime.parse(req.getHoraFin())    : null)
                .motivo(req.getMotivo())
                .build();

        bloqueoRepository.save(bloqueo);
    }

    // ─────────────────────────────────────────────────────────
    // DELETE /api/citas/psicologo/{id}/dias-no-disponibles/{fecha}
    // ─────────────────────────────────────────────────────────
    @Transactional
    public void removeBloqueo(Long idPsicologo, String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        bloqueoRepository.deleteByPsicologoIdPsicologoAndFecha(idPsicologo, fecha);
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────
    private AgendaItemDTO citaToAgendaItem(Cita c) {
        LocalDateTime start = c.getStartDatetime();
        return AgendaItemDTO.builder()
                .id(c.getIdCita())
                .fecha(start.toLocalDate())
                .horaInicio(start.toLocalTime())
                .horaFin(start.plusMinutes(c.getDurationMinutes()).toLocalTime())
                .tipo("cita")
                .estado(c.getEstado().name())
                .motivo(c.getMotivo())
                .duracionMinutos(c.getDurationMinutes())
                .nombrePaciente(nombreCompleto(c.getPaciente().getUsuario()))
                .nombrePsicologo(nombreCompleto(c.getPsicologo().getUsuario()))
                .build();
    }

    private AgendaItemDTO bloqueoToAgendaItem(BloqueoAgenda b) {
        return AgendaItemDTO.builder()
                .id(b.getIdBloqueo())
                .fecha(b.getFecha())
                .horaInicio(b.getHoraInicio() != null ? b.getHoraInicio() : LocalTime.of(0, 0))
                .horaFin(b.getHoraFin()       != null ? b.getHoraFin()   : LocalTime.of(23, 59))
                .tipo("bloqueo")
                .motivo(b.getMotivo())
                .build();
    }

    private String nombreCompleto(Usuario u) {
        return u.getNombre() + (u.getApellido() != null ? " " + u.getApellido() : "");
    }

    /** Devuelve [inicioMes, finMes) como LocalDateTime */
    private LocalDateTime[] rangoMes(String month) {
        YearMonth ym     = YearMonth.parse(month);
        LocalDateTime ini = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().plusDays(1).atStartOfDay();
        return new LocalDateTime[]{ini, fin};
    }
}
