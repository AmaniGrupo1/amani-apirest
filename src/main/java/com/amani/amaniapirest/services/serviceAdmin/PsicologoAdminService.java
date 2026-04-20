package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.dtoAdmin.TutorResonseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.DireccionResponseDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PacientePsicologoResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.PacientesAsignadoDTO;
import com.amani.amaniapirest.dto.loginDTO.PsicologoConPacientesDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PsicologoAdminService {

    private final PsicologoRepository psicologoRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;

    // Actualizar psicólogo
    public PsicologoConPacientesDTO update(Long idPsicologo, PsicologoRequestDTO request) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);

        psicologo.setEspecialidad(request.getEspecialidad());
        psicologo.setExperiencia(request.getExperiencia());
        psicologo.setDescripcion(request.getDescripcion());
        psicologo.setLicencia(request.getLicencia());

        psicologoRepository.save(psicologo);

        // Retornamos info del psicólogo actualizado sin pacientes por simplicidad
        return mapToDto(psicologo);
    }

    // Eliminar psicólogo
    public void delete(Long idPsicologo) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        psicologoRepository.delete(psicologo);
    }

    // Listar psicólogos con sus pacientes
    public List<PsicologoConPacientesDTO> getPsicologosConPacientes() {
        List<Psicologo> psicologos = psicologoRepository.findAll();
        List<PsicologoConPacientesDTO> result = new ArrayList<>();

        for (Psicologo psicologo : psicologos) {
            // Traer pacientes activos asignados a este psicólogo
            List<PacientesAsignadoDTO> pacientes = psicologoPacienteRepository
                    .findByPsicologoIdPsicologoAndFechaFinIsNull(psicologo.getIdPsicologo())
                    .stream()
                    .map(pp -> {
                        var usuarioPaciente = pp.getPaciente().getUsuario();
                        return new PacientesAsignadoDTO(
                                pp.getPaciente().getIdPaciente(),
                                usuarioPaciente.getNombre(),
                                usuarioPaciente.getApellido(),
                                usuarioPaciente.getEmail()
                        );
                    })
                    .toList();
            System.out.println("Psicologo " + psicologo.getIdPsicologo() + " tiene pacientes: " + pacientes.size());
            result.add(new PsicologoConPacientesDTO(
                    psicologo.getIdPsicologo(),
                    psicologo.getUsuario().getNombre(),
                    psicologo.getUsuario().getApellido(),
                    psicologo.getUsuario().getEmail(),
                    psicologo.getEspecialidad(),
                    psicologo.getLicencia(),
                    psicologo.getUpdatedAt(),
                    pacientes
            ));
        }


        return result;
    }

    // ---------------- Helper ----------------
    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con id: " + idPsicologo));
    }

    private PsicologoConPacientesDTO mapToDto(Psicologo psicologo) {
        List<PacientesAsignadoDTO> pacientes = psicologoPacienteRepository
                .findByPsicologoIdPsicologoAndFechaFinIsNull(psicologo.getIdPsicologo())
                .stream()
                .map(pp -> {
                    var u = pp.getPaciente().getUsuario();
                    return new PacientesAsignadoDTO(
                            pp.getPaciente().getIdPaciente(),
                            u.getNombre(),
                            u.getApellido(),
                            u.getEmail()
                    );
                })
                .toList();

        return new PsicologoConPacientesDTO(
                psicologo.getIdPsicologo(),
                psicologo.getUsuario().getNombre(),
                psicologo.getUsuario().getApellido(),
                psicologo.getUsuario().getEmail(),
                psicologo.getEspecialidad(),
                psicologo.getLicencia(),
                psicologo.getUpdatedAt(),
                pacientes
        );
    }


    // Otros métodos relacionados con psicólogos y pacientes pueden ir aquí

    //OBTNER PSICOLOGO LOGUEADO
    public Psicologo getPsicologoLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // email del JWT

        return psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado con email: " + email));
    }

    public List<PacientePsicologoResponseDTO> getPacientesDelPsicologoLogueado() {
        Psicologo psicologo = getPsicologoLogueado();

        return psicologoPacienteRepository
                .findPacientesConTutores(psicologo.getIdPsicologo()) // 👈 IMPORTANTE (JOIN FETCH)
                .stream()
                .map(pp -> mapPaciente(pp.getPaciente()))
                .toList();
    }

    private PacientePsicologoResponseDTO mapPaciente(Paciente paciente) {
        var usuario = paciente.getUsuario();

        // ---------------- DIRECCIÓN ----------------
        DireccionResponseDTO direccion = null;
        if (paciente.getDirecciones() != null && !paciente.getDirecciones().isEmpty()) {
            var d = paciente.getDirecciones().get(0);
            direccion = new DireccionResponseDTO(
                    d.getCalle(),
                    d.getCiudad(),
                    d.getProvincia(),
                    d.getCodigoPostal(),
                    d.getPais()
            );
        }

        // ---------------- PRÓXIMA CITA ----------------
        var cita = (paciente.getCitas() != null && !paciente.getCitas().isEmpty())
                ? paciente.getCitas().stream()
                .filter(c -> "pendiente".equals(c.getEstado()) || "confirmada".equals(c.getEstado()))
                .sorted((c1, c2) -> c1.getStartDatetime().compareTo(c2.getStartDatetime()))
                .findFirst()
                .orElse(null)
                : null;

        LocalTime horaInicio = (cita != null) ? cita.getStartDatetime().toLocalTime() : null;
        LocalTime horaFin = (cita != null)
                ? cita.getStartDatetime().plusMinutes(cita.getDurationMinutes()).toLocalTime()
                : null;

        // ---------------- DTO ----------------
        PacientePsicologoResponseDTO dto = new PacientePsicologoResponseDTO();

        dto.setIdPaciente(paciente.getIdPaciente());
        dto.setIdUsuario(paciente.getUsuario().getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setDni(usuario.getDni());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setEmail(usuario.getEmail());
        dto.setGenero(paciente.getGenero());
        dto.setTelefono(paciente.getTelefono());
        dto.setDireccion(direccion);
        dto.setHoraInicio(horaInicio);
        dto.setHoraFin(horaFin);

        // ---------------- TUTORES (CORREGIDO) ----------------
        dto.setTutor(
                paciente.getTutores() == null
                        ? new ArrayList<>()
                        : paciente.getTutores()
                        .stream()
                        .map(t -> new TutorResonseDTO(
                                t.getIdTutor(),// 👈 si existe en entidad
                                t.getNombre(),
                                t.getDni(),
                                t.getTelefono(),
                                t.getEmail(),
                                t.getTipo()
                        ))
                        .toList()
        );

        return dto;
    }
}