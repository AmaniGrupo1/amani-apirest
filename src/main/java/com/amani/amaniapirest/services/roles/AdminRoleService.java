package com.amani.amaniapirest.services.roles;


import com.amani.amaniapirest.dto.roles.CambiarRolRequestDTO;
import com.amani.amaniapirest.dto.roles.CambiarRolResponseDTO;
import com.amani.amaniapirest.dto.roles.UsuarioDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final UsuarioRepository usuarioRepository;
    private final PacientesRepository pacienteRepository;
    private final PsicologoRepository psicologoRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;

    @Transactional
    public CambiarRolResponseDTO cambiarRol(CambiarRolRequestDTO request) {

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RolUsuario rolAnterior = usuario.getRol();
        RolUsuario nuevoRol = request.getNuevoRol();

        // ====================================
        // ELIMINAR PERFIL ANTERIOR
        // ====================================

        // Si era paciente
        if (usuario.getPaciente() != null) {

            Long idPaciente = usuario.getPaciente().getIdPaciente();

            // eliminar relaciones con psicólogos
            psicologoPacienteRepository.deleteByPacienteId(idPaciente);

            // eliminar perfil paciente
            pacienteRepository.delete(usuario.getPaciente());

            usuario.setPaciente(null);
        }

        // Si era psicólogo
        if (usuario.getPsicologo() != null) {

            psicologoRepository.delete(usuario.getPsicologo());

            usuario.setPsicologo(null);
        }

        // ====================================
        // CAMBIAR ROL
        // ====================================

        usuario.setRol(nuevoRol);

        usuarioRepository.save(usuario);

        // ====================================
        // CREAR NUEVO PERFIL
        // ====================================

        switch (nuevoRol) {

            case psicologo -> {

                Psicologo psicologo = new Psicologo();

                psicologo.setUsuario(usuario);
                psicologo.setEspecialidad("General");

                psicologoRepository.save(psicologo);
            }

            case paciente -> {

                Paciente paciente = new Paciente();

                paciente.setUsuario(usuario);

                pacienteRepository.save(paciente);
            }

            case admin -> {
                // no necesita perfil extra
            }
        }

        return CambiarRolResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rolAnterior(rolAnterior)
                .nuevoRol(nuevoRol)
                .mensaje("Rol actualizado correctamente")
                .build();
    }

    public List<UsuarioDTO> getUsuarios(String rol, String dni) {

        // Parse the incoming `rol` parameter in a case-insensitive and safe way.
        // The enum `RolUsuario` uses lowercase names (admin, psicologo, paciente),
        // so valueOf(rol.toUpperCase()) will fail. We map ignoring case and
        // throw IllegalArgumentException when the value is invalid so the
        // controller can return a 400 Bad Request.
        RolUsuario rolEnum = null;

        if (rol != null && !rol.isBlank()) {
            rolEnum = java.util.Arrays.stream(RolUsuario.values())
                    .filter(r -> r.name().equalsIgnoreCase(rol))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Rol inválido: " + rol));
        }

        // Avoid JPQL null-parameter typing issues in Postgres by using
        // Spring Data derived queries when possible instead of a single
        // JPQL query with nullable enum parameters.
        List<Usuario> usuarios;
        boolean hasRol = rolEnum != null;
        boolean hasDni = dni != null && !dni.isBlank();

        if (hasRol && hasDni) {
            usuarios = usuarioRepository.findByRolAndDniContaining(rolEnum, dni);
        } else if (hasRol) {
            usuarios = usuarioRepository.findByRol(rolEnum);
        } else if (hasDni) {
            usuarios = usuarioRepository.findByDniContaining(dni);
        } else {
            usuarios = usuarioRepository.findAll();
        }

        return usuarios.stream()
                .map(u -> new UsuarioDTO(
                        u.getIdUsuario(),
                        u.getNombre(),
                        u.getApellido(),
                        u.getEmail(),
                        u.getDni(),
                        u.getRol(),
                        u.getActivo(),
                        u.getPsicologo() != null ? u.getPsicologo().getIdPsicologo() : null,
                        u.getPaciente() != null ? u.getPaciente().getIdPaciente() : null
                ))
                .collect(Collectors.toList());
    }
}
