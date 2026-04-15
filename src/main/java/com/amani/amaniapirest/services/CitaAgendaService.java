    package com.amani.amaniapirest.services;

    import com.amani.amaniapirest.dto.dtoPaciente.request.CitaRequestDTO;
    import com.amani.amaniapirest.dto.dtoPaciente.response.CitaResponseDTO;
    import com.amani.amaniapirest.dto.terapiasDTO.TerapiaResponseDTO;
    import com.amani.amaniapirest.enums.EstadoPago;
    import com.amani.amaniapirest.models.*;
    import com.amani.amaniapirest.repositories.BloqueoAgendaRepository;
    import com.amani.amaniapirest.repository.CitaRepository;
    import com.amani.amaniapirest.repository.HorarioPsicologoRepository;
    import com.amani.amaniapirest.repository.PacientesRepository;
    import com.amani.amaniapirest.repository.PsicologoRepository;
    import com.amani.amaniapirest.dto.dtoAgenda.request.BloqueoRequestDTO;
    import com.amani.amaniapirest.dto.dtoAgenda.request.HorarioRequestDTO;
    import com.amani.amaniapirest.dto.dtoAgenda.response.AgendaItemDTO;
    import com.amani.amaniapirest.dto.dtoAgenda.response.DisponibilidadDTO;
    import com.amani.amaniapirest.dto.dtoAgenda.response.SlotDTO;
    import com.amani.amaniapirest.enums.EstadoCita;
    import com.amani.amaniapirest.repository.terapiaService.TerapiaRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.context.SecurityContextHolder;
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
    @RequiredArgsConstructor
    public class CitaAgendaService {

        private static final int SLOT_MINUTOS = 50;

        private final CitaRepository citaRepository;
        private final HorarioPsicologoRepository horarioRepository;
        private final BloqueoAgendaRepository bloqueoRepository;
        private final PsicologoRepository psicologoRepository;
        private final PacientesRepository pacienteRepository;
        private final CitaService citaService;
        private final TerapiaRepository terapiaRepository;

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
        // PATCH /api/citas/psicologo/{id}/duracion
        // Permite actualizar la duración por defecto de las citas para un psicólogo
        // ─────────────────────────────────────────────────────────
        @Transactional
        public void actualizarDuracionPsicologo(Long idPsicologo, Integer duracion) {

            Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                    .orElseThrow(() -> new NoSuchElementException("Psicólogo no encontrado"));

            psicologo.setDuracionDefault(duracion);

            psicologoRepository.save(psicologo);
        }
        // ─────────────────────────────────────────────────────────
        // GET /api/citas/psicologo/{id}/duracion
        // Devuelve la duración por defecto de las citas para un psicólogo
        // ─────────────────────────────────────────────────────────

        public Integer getDuracionDefault(Long idPsicologo) {
            return psicologoRepository.findById(idPsicologo)
                    .orElseThrow()
                    .getDuracionDefault();
        }

        // ─────────────────────────────────────────────────────────
        // GET /api/citas/psicologo/{id}/agenda?month=YYYY-MM
        // ─────────────────────────────────────────────────────────
        public List<AgendaItemDTO> getAgendaPsicologo(Long idPsicologo, String month) {

            String email = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();

            Psicologo logueado = psicologoRepository.findByUsuarioEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("Psicólogo no encontrado"));

            // 🔐 VALIDACIÓN IMPORTANTE
            if (!logueado.getIdPsicologo().equals(idPsicologo)) {
                throw new SecurityException("No tienes acceso a esta agenda");
            }

            var rango = rangoMes(month);

            List<AgendaItemDTO> items = new ArrayList<>();

            citaRepository.findByPsicologo_IdPsicologoAndStartDatetimeBetween(
                    idPsicologo, rango[0], rango[1]
            ).stream().map(this::citaToAgendaItem).forEach(items::add);

            LocalDate inicio = rango[0].toLocalDate();
            LocalDate fin = rango[1].toLocalDate();

            bloqueoRepository.findByPsicologoIdPsicologoAndFechaBetween(idPsicologo, inicio, fin)
                    .stream().map(this::bloqueoToAgendaItem).forEach(items::add);

            items.sort(Comparator.comparing(AgendaItemDTO::getFecha)
                    .thenComparing(AgendaItemDTO::getHoraInicio));

            return items;
        }

        // ─────────────────────────────────────────────────────────
        // GET /api/citas/psicologo/{id}/disponibilidad?fecha=YYYY-MM-DD
        // Versión original sin parámetro de duración (mantiene compatibilidad)
        // ─────────────────────────────────────────────────────────
        public DisponibilidadDTO getDisponibilidad(Long idPsicologo, String fechaStr) {
            return getDisponibilidadConDuracion(idPsicologo, fechaStr, SLOT_MINUTOS);
        }

        // ─────────────────────────────────────────────────────────
        // Nuevo método sobrecargado que acepta duración personalizada
        // GET /api/citas/psicologo/{id}/disponibilidad?fecha=YYYY-MM-DD&duracion=60
        // ─────────────────────────────────────────────────────────
        public DisponibilidadDTO getDisponibilidadConDuracion(Long idPsicologo, String fechaStr, Integer duracionMinutos) {
            LocalDate fecha = LocalDate.parse(fechaStr);
            int duracionSlot = resolverDuracion(idPsicologo, duracionMinutos);

            // Verificar si el día está completamente bloqueado
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

            // Obtener franjas activas del psicólogo para el día de la semana
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

            // Generar slots ocupados: citas y bloqueos parciales
            Set<LocalTime> ocupados = new HashSet<>();

            // Citas confirmadas / pendientes
            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();
            citaRepository.findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
                    idPsicologo, inicioDia, finDia, EstadoCita.cancelada
            ).forEach(c -> {
                LocalTime start = c.getStartDatetime().toLocalTime();
                LocalTime end = start.plusMinutes(c.getDurationMinutes());

                for (LocalTime t = start; t.isBefore(end); t = t.plusMinutes(duracionSlot)) {
                    ocupados.add(t);
                }
            });

            // Bloqueos parciales
            bloqueoRepository.findByPsicologoIdPsicologoAndFecha(idPsicologo, fecha)
                    .stream()
                    .filter(b -> b.getHoraInicio() != null && b.getHoraFin() != null)
                    .forEach(b -> {
                        LocalTime t = b.getHoraInicio();
                        while (t.isBefore(b.getHoraFin())) {
                            ocupados.add(t);
                            t = t.plusMinutes(duracionSlot);
                        }
                    });
            // Generar slots libres según las franjas y duración solicitada
            List<SlotDTO> libres = new ArrayList<>();
            for (HorarioPsicologo franja : franjas) {
                LocalTime t = franja.getHoraInicio();
                while (!t.plusMinutes(duracionSlot).isAfter(franja.getHoraFin())) {
                    if (!ocupados.contains(t)) {
                        libres.add(SlotDTO.builder()
                                .hora(t)
                                .horaFin(t.plusMinutes(duracionSlot))
                                .build());
                    }
                    t = t.plusMinutes(duracionSlot);
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
        public AgendaItemDTO crearCita(CitaRequestDTO req) {

            LocalDateTime start = req.getStartDatetime();
            Long idPsicologo = req.getIdPsicologo();
            Long idPaciente = req.getIdPaciente();

            int duracion = resolverDuracion(idPsicologo, req.getDurationMinutes());
            LocalDateTime end = start.plusMinutes(duracion);

            Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                    .orElseThrow(() -> new NoSuchElementException("Psicólogo no encontrado"));

            Paciente paciente = pacienteRepository.findById(idPaciente)
                    .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

            short diaSemana = (short) (start.getDayOfWeek().getValue() - 1);

            // 1. VALIDACIONES (igual que ya tienes)
            boolean dentroHorario = horarioRepository
                    .findByPsicologoIdPsicologoAndActivoTrue(idPsicologo)
                    .stream()
                    .filter(h -> h.getDiaSemana() == diaSemana)
                    .anyMatch(h ->
                            !start.toLocalTime().isBefore(h.getHoraInicio()) &&
                                    !end.toLocalTime().isAfter(h.getHoraFin())
                    );

            if (!dentroHorario) {
                throw new IllegalStateException("Fuera del horario del psicólogo");
            }

            // 2. BLOQUEOS
            boolean bloqueado = bloqueoRepository
                    .findByPsicologoIdPsicologoAndFecha(idPsicologo, start.toLocalDate())
                    .stream()
                    .anyMatch(b -> b.getHoraInicio() == null || b.getHoraFin() == null ||
                            !(end.toLocalTime().isBefore(b.getHoraInicio()) ||
                                    start.toLocalTime().isAfter(b.getHoraFin()))
                    );

            if (bloqueado) {
                throw new IllegalStateException("Horario bloqueado");
            }

            // 3. SOLAPAMIENTO
            LocalDateTime inicioDia = start.toLocalDate().atStartOfDay();
            LocalDateTime finDia = start.toLocalDate().plusDays(1).atStartOfDay();

            boolean conflicto = citaRepository
                    .findByPsicologo_IdPsicologoAndStartDatetimeBetweenAndEstadoNot(
                            idPsicologo, inicioDia, finDia, EstadoCita.cancelada
                    )
                    .stream()
                    .anyMatch(c ->
                            !(c.getStartDatetime().isAfter(end) ||
                                    c.getStartDatetime().plusMinutes(c.getDurationMinutes()).isBefore(start))
                    );

            if (conflicto) {
                throw new IllegalStateException("Ya existe una cita en ese horario");
            }

            // 4. CREAR CITA
            Cita cita = new Cita();
            cita.setPaciente(paciente);
            cita.setPsicologo(psicologo);
            cita.setStartDatetime(start);
            cita.setDurationMinutes(duracion);
            cita.setMotivo(req.getMotivo());
            cita.setEstado(req.getEstado() != null ? req.getEstado() : EstadoCita.pendiente);
            cita.setCreatedAt(LocalDateTime.now());

            // IMPORTANTE: guardar primero cita
            cita = citaRepository.save(cita);

            // 5. CREAR PAGO
            Pago pago = new Pago();
            pago.setCita(cita);
            pago.setMonto(req.getMonto());
            pago.setMetodoPago(req.getMetodoPago());
            pago.setEstadoPago(req.getEstadoPago() != null ? req.getEstadoPago() : EstadoPago.PENDIENTE);

            // opcional: si ya viene pagado
            if (pago.getEstadoPago() == EstadoPago.PAGADO) {
                pago.setFechaPago(LocalDateTime.now());
            }

            cita.setPago(pago);

            // Cascade ALL en Cita -> Pago, así que esto guarda ambos
            citaRepository.save(cita);

            return citaToAgendaItem(cita);
        }


        // ─────────────────────────────
        // Método auxiliar para resolver la duración a usar en la disponibilidad

        // ─────────────────────────────

        private int resolverDuracion(Long idPsicologo, Integer requestDuracion) {

            if (requestDuracion != null && requestDuracion > 0) {
                return requestDuracion;
            }

            Integer def = psicologoRepository.findById(idPsicologo)
                    .orElseThrow()
                    .getDuracionDefault();

            if (def != null && def > 0) {
                return def;
            }

            return SLOT_MINUTOS;
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

            for (var franja : req.getFranjas()) {

                short dia = franja.getDiaSemana();

                horarioRepository.deleteByPsicologoIdPsicologoAndDiaSemana(idPsicologo, dia);

                HorarioPsicologo nuevo = HorarioPsicologo.builder()
                        .psicologo(psicologo)
                        .diaSemana(dia)
                        .horaInicio(LocalTime.parse(franja.getHoraInicio()))
                        .horaFin(LocalTime.parse(franja.getHoraFin()))
                        .activo(franja.isActivo())
                        .build();

                horarioRepository.save(nuevo);
            }
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
                    .horaFin(req.getHoraFin() != null ? LocalTime.parse(req.getHoraFin()) : null)
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

            TerapiaResponseDTO terapiaDTO = null;

            if (c.getTipoTerapia() != null) {
                terapiaDTO = new TerapiaResponseDTO(
                        c.getTipoTerapia().getIdTipo(),
                        c.getTipoTerapia().getNombre(),
                        c.getTipoTerapia().getDuracionMinutos()
                );
            }

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
                    .terapia(terapiaDTO) // 🔥 AQUÍ ESTÁ LA MAGIA
                    .build();
        }

        private AgendaItemDTO bloqueoToAgendaItem(BloqueoAgenda b) {
            return AgendaItemDTO.builder()
                    .id(b.getIdBloqueo())
                    .fecha(b.getFecha())
                    .horaInicio(b.getHoraInicio() != null ? b.getHoraInicio() : LocalTime.of(0, 0))
                    .horaFin(b.getHoraFin() != null ? b.getHoraFin() : LocalTime.of(23, 59))
                    .tipo("bloqueo")
                    .motivo(b.getMotivo())
                    .build();
        }

        private String nombreCompleto(Usuario u) {
            return u.getNombre() + (u.getApellido() != null ? " " + u.getApellido() : "");
        }

        /**
         * Devuelve [inicioMes, finMes) como LocalDateTime
         */
        private LocalDateTime[] rangoMes(String month) {
            YearMonth ym = YearMonth.parse(month);
            LocalDateTime ini = ym.atDay(1).atStartOfDay();
            LocalDateTime fin = ym.atEndOfMonth().plusDays(1).atStartOfDay();
            return new LocalDateTime[]{ini, fin};
        }


        public HorarioRequestDTO getHorarioActual(Long idPsicologo) {

            List<HorarioRequestDTO.FranjaHorarioDTO> franjas = horarioRepository
                    .findByPsicologoIdPsicologoAndActivoTrue(idPsicologo)
                    .stream()
                    .map(h -> new HorarioRequestDTO.FranjaHorarioDTO(
                            h.getDiaSemana(),
                            h.getHoraInicio().toString(),
                            h.getHoraFin().toString(),
                            h.isActivo(),
                            null
                    ))
                    .toList();

            return new HorarioRequestDTO(franjas);
        }
    }