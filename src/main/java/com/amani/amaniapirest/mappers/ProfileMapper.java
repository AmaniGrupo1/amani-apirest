package com.amani.amaniapirest.mappers;

import com.amani.amaniapirest.dto.profile.paciente.PacienteDTO;
import com.amani.amaniapirest.dto.profile.psicologo.PsicologoDTO;
import com.amani.amaniapirest.dto.profile.UsuarioDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    public UsuarioDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;

        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl() // solo los datos que quieres mostrar
        );
    }

    public PsicologoDTO toPsicologoDTO(Psicologo psicologo) {
        if (psicologo == null) return null;

        return new PsicologoDTO(
                psicologo.getIdPsicologo(),
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia(),
                toUsuarioDTO(psicologo.getUsuario())
        );
    }

    public PacienteDTO toPacienteDTO(Paciente paciente) {
        if (paciente == null) return null;

        return new PacienteDTO(
                paciente.getIdPaciente(),
                paciente.getTelefono(),
                paciente.getGenero(),
                paciente.getFechaNacimiento(),
                toUsuarioDTO(paciente.getUsuario())
        );
    }
}