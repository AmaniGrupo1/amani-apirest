package com.amani.amaniapirest.services.serviceAdmin;

import com.amani.amaniapirest.dto.loginDTO.PacientesAsignadoDTO;
import com.amani.amaniapirest.dto.loginDTO.PsicologoConPacientesDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}