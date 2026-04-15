package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.TutorResonseDTO;
import com.amani.amaniapirest.dto.dtoAdmin.response.PacienteAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.UsuarioRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.amani.amaniapirest.models.*;
import com.amani.amaniapirest.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteAdminService {

    private final PacientesRepository pacientesRepository;
    private final UsuarioRepository usuarioRepository;
    private final PsicologoRepository psicologoRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;
    private final SituacionRepository situacionRepository;
    private final PacienteSituacionRepository pacienteSituacionRepository;

    /** Listar todos los pacientes */
    public List<PacienteAdminResponseDTO> findAll() {
        return pacientesRepository.findAllWithSituacionesTutoresYDirecciones().stream()
                .map(this::toResponse)
                .toList();
    }

    /** Buscar paciente por ID */
    public PacienteAdminResponseDTO findById(Long idPaciente) {
        return toResponse(getPacienteOrThrow(idPaciente));
    }

    /** ------------------- Asignar psicólogo ------------------- */
    @Transactional
    public boolean asignarPsicologo(Long pacienteId, Long psicologoId) {
        // Obtener paciente
        Paciente paciente = pacientesRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Obtener psicólogo
        Psicologo psicologo = psicologoRepository.findById(psicologoId)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        // Comprobar si ya existe asignación activa
        PsicologoPaciente asignacionActual = psicologoPacienteRepository
                .findByPacienteIdPacienteAndFechaFinIsNull(pacienteId);

        if (asignacionActual != null) {
            // Cerrar la asignación anterior
            asignacionActual.setFechaFin(LocalDateTime.now());
            psicologoPacienteRepository.save(asignacionActual);
        }

        // Crear nueva asignación
        PsicologoPaciente nuevaAsignacion = new PsicologoPaciente();
        nuevaAsignacion.setPaciente(paciente);
        nuevaAsignacion.setPsicologo(psicologo);
        nuevaAsignacion.setFechaInicio(LocalDateTime.now());
        nuevaAsignacion.setFechaFin(null);
        psicologoPacienteRepository.save(nuevaAsignacion);

        // Actualizar referencia en paciente
        paciente.setPsicologo(psicologo);
        pacientesRepository.save(paciente);

        return true; // devuelve boolean
    }
    /** Crear paciente con usuario y situaciones opcionales */
    @Transactional
    public PacienteAdminResponseDTO create(PacienteRequestDTO request) {

        Usuario usuario;

        // --- Crear usuario si se envía info ---
        if (request.getUsuario() != null) {
            UsuarioRequestDTO u = request.getUsuario();
            usuario = new Usuario();
            usuario.setNombre(u.getNombre());
            usuario.setApellido(u.getApellido());
            usuario.setEmail(u.getEmail());
            usuario.setPassword(new BCryptPasswordEncoder().encode(u.getPassword()));
            usuario.setRol(u.getRol());
            usuario.setActivo(u.getActivo() != null ? u.getActivo() : true);

            usuario = usuarioRepository.save(usuario);
        } else {
            usuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        // --- Crear paciente ---
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setCreatedAt(LocalDateTime.now());

        paciente = pacientesRepository.save(paciente);

        // --- Registrar motivos/situaciones si vienen IDs ---
        if (request.getIdSituaciones() != null && !request.getIdSituaciones().isEmpty()) {
            Set<Situacion> situaciones = request.getIdSituaciones().stream()
                    .map(id -> situacionRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Situación no encontrada: " + id)))
                    .collect(Collectors.toSet());

            for (Situacion s : situaciones) {
                PacienteSituacion ps = new PacienteSituacion();
                ps.setPaciente(paciente);
                ps.setSituacion(s);
                ps.setFechaRegistro(LocalDateTime.now());
                pacienteSituacionRepository.save(ps);
            }
        }

        return toResponse(paciente);
    }

    /** Actualizar paciente */
    @Transactional
    public PacienteAdminResponseDTO update(Long idPaciente, PacienteRequestDTO request) {
        Paciente paciente = getPacienteOrThrow(idPaciente);
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());

        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        // --- Actualizar situaciones ---
        if (request.getIdSituaciones() != null && !request.getIdSituaciones().isEmpty()) {
            // Borrar relaciones anteriores
            pacienteSituacionRepository.deleteAllByPaciente_IdPaciente(paciente.getIdPaciente());

            // Guardar nuevas relaciones
            Set<Situacion> nuevasSituaciones = request.getIdSituaciones().stream()
                    .map(id -> situacionRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Situación no encontrada: " + id)))
                    .collect(Collectors.toSet());

            for (Situacion s : nuevasSituaciones) {
                PacienteSituacion ps = new PacienteSituacion();
                ps.setPaciente(paciente);
                ps.setSituacion(s);
                ps.setFechaRegistro(LocalDateTime.now());
                pacienteSituacionRepository.save(ps);
            }
        }

        return toResponse(pacientesRepository.save(paciente));
    }

    /** ------------------- Helpers ------------------- */
    private Paciente getPacienteOrThrow(Long idPaciente) {
        return pacientesRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
    }

    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    /** Convertir paciente a DTO de respuesta incluyendo situaciones */
    private PacienteAdminResponseDTO toResponse(Paciente paciente) {

        String estadoPago = null;
        String metodoPago = null;

        if (paciente.getCitas() != null && !paciente.getCitas().isEmpty()) {

            // obtener último pago de la última cita
            Cita ultimaCita = paciente.getCitas()
                    .stream()
                    .max((a, b) -> a.getStartDatetime().compareTo(b.getStartDatetime()))
                    .orElse(null);

            if (ultimaCita != null && ultimaCita.getPago() != null) {
                estadoPago = ultimaCita.getPago().getEstadoPago().name();
                metodoPago = ultimaCita.getPago().getMetodoPago().name();
            }
        }

        // --- Mapear situaciones ---
        List<SituacionDTO> situaciones = paciente.getPacienteSituaciones().stream()
                .map(ps -> new SituacionDTO(
                        ps.getSituacion().getIdSituacion(),
                        ps.getSituacion().getNombre(),
                        ps.getSituacion().getCategoria(),
                        ps.getSituacion().getDescripcion()
                ))
                .toList();

        // --- Calcular si es menor ---
        boolean esMenor = paciente.getFechaNacimiento() != null &&
                Period.between(paciente.getFechaNacimiento(), LocalDate.now()).getYears() < 18;

        // --- Mapear tutores solo si es menor ---
        List<TutorResonseDTO> tutores = esMenor && paciente.getTutores() != null
                ? paciente.getTutores().stream()
                .map(t -> new TutorResonseDTO(
                        t.getIdTutor(),
                        t.getNombre(),
                        t.getTelefono(),
                        t.getEmail(),
                        t.getDni(),
                        t.getTipo()
                ))
                .toList()
                : List.of(); // vacío si no es menor o no tiene tutores

        List<DireccionResponseDTO> direcciones = paciente.getDirecciones() != null
                ? paciente.getDirecciones().stream()
                .map(d -> new DireccionResponseDTO(
                        d.getCalle(),
                        d.getCiudad(),
                        d.getProvincia(),
                        d.getCodigoPostal(),
                        d.getPais()
                ))
                .toList()
                : List.of();

        return new PacienteAdminResponseDTO(
                paciente.getIdPaciente(),
                paciente.getUsuario().getNombre(),
                paciente.getUsuario().getApellido(),
                paciente.getUsuario().getEmail(),
                paciente.getFechaNacimiento(),
                paciente.getGenero(),
                paciente.getTelefono(),
                paciente.getCreatedAt(),
                paciente.getUpdatedAt(),
                paciente.getUsuario().getActivo(),
                estadoPago,
                metodoPago,
                situaciones,
                tutores,
                direcciones
        );
    }
}