package com.amani.amaniapirest.mappers;

import com.amani.amaniapirest.dto.profile.PacienteDTO;
import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

    public UsuarioDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().name() : null,
                usuario.getActivo()
        );
    }

    public PacienteDTO toPacienteDTO(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        Long idPsicologo = paciente.getPsicologo() != null ? paciente.getPsicologo().getIdPsicologo() : null;
        return new PacienteDTO(
                paciente.getIdPaciente(),
                paciente.getFechaNacimiento(),
                paciente.getGenero(),
                paciente.getTelefono(),
                idPsicologo
        );
    }

    public PsicologoDTO toPsicologoDTO(Psicologo psicologo, List<Long> pacientes) {
        if (psicologo == null) {
            return null;
        }
        return new PsicologoDTO(
                psicologo.getIdPsicologo(),
                psicologo.getEspecialidad(),
                psicologo.getLicencia(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                pacientes
        );
    }
}
