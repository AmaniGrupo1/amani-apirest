package com.amani.amaniapirest.services.roles;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.roles.CambiarRolRequestDTO;
import com.amani.amaniapirest.dto.roles.CambiarRolResponseDTO;
import com.amani.amaniapirest.dto.roles.UsuarioDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.AjusteRepository;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoPacienteRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtUtil jwtUtil;
    private final AjusteRepository ajustesRepository;
    private final SecurityConfig securityConfig;

    @Transactional
    public CambiarRolResponseDTO cambiarRol(CambiarRolRequestDTO request) {

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RolUsuario rolAnterior = usuario.getRol();
        RolUsuario nuevoRol = request.getNuevoRol();

        // ====================================
        // VALIDAR CAMBIO INNECESARIO
        // ====================================

        if (rolAnterior == nuevoRol) {
            throw new RuntimeException("El usuario ya tiene ese rol");
        }

        // ====================================
        // VALIDAR PSICÓLOGO CON PACIENTES
        // ====================================

        if (usuario.getPsicologo() != null) {

            Long idPsicologo =
                    usuario.getPsicologo().getIdPsicologo();

            boolean tienePacientes =
                    psicologoPacienteRepository
                            .existsByPsicologo_IdPsicologo(idPsicologo);

            if (tienePacientes && nuevoRol == RolUsuario.admin) {

                throw new RuntimeException(
                        "No puedes cambiar el rol porque el psicólogo tiene pacientes asignados"
                );
            }
        }

        // ====================================
        // VALIDAR PACIENTE CON PSICÓLOGO
        // ====================================

        if (usuario.getPaciente() != null) {

            Long idPaciente =
                    usuario.getPaciente().getIdPaciente();

            boolean tienePsicologo =
                    psicologoPacienteRepository
                            .existsByPaciente_IdPaciente(idPaciente);

            if (tienePsicologo && nuevoRol == RolUsuario.admin) {

                throw new RuntimeException(
                        "No puedes cambiar el rol porque el paciente tiene un psicólogo asignado"
                );
            }
        }

        // ====================================
        // ELIMINAR PERFIL PACIENTE
        // ====================================

        if (usuario.getPaciente() != null) {

            Long idPaciente =
                    usuario.getPaciente().getIdPaciente();

            psicologoPacienteRepository
                    .deleteByPacienteId(idPaciente);

            pacienteRepository.delete(usuario.getPaciente());

            usuario.setPaciente(null);
        }

        // ====================================
        // ELIMINAR PERFIL PSICÓLOGO
        // ====================================

        if (usuario.getPsicologo() != null) {

            Long idPsicologo =
                    usuario.getPsicologo().getIdPsicologo();

            psicologoPacienteRepository
                    .deleteByPsicologo_IdPsicologo(idPsicologo);

            psicologoRepository.delete(usuario.getPsicologo());

            usuario.setPsicologo(null);
        }

        // ====================================
        // CAMBIAR ROL
        // ====================================

        usuario.setRol(nuevoRol);

        usuarioRepository.save(usuario);

        Long idPsicologo = null;
        Long idPaciente = null;

        // ====================================
        // CREAR NUEVO PERFIL
        // ====================================

        switch (nuevoRol) {

            case psicologo -> {

                Psicologo psicologo = new Psicologo();

                psicologo.setUsuario(usuario);
                psicologo.setEspecialidad("General");
                psicologo.setExperiencia(0);

                Psicologo psicologoGuardado =
                        psicologoRepository.save(psicologo);

                idPsicologo =
                        psicologoGuardado.getIdPsicologo();
            }

            case paciente -> {

                Paciente paciente = new Paciente();

                paciente.setUsuario(usuario);

                Paciente pacienteGuardado =
                        pacienteRepository.save(paciente);

                idPaciente =
                        pacienteGuardado.getIdPaciente();
            }

            case admin -> {
                // admin no necesita perfil
            }
        }

        // ====================================
        // GENERAR NUEVO TOKEN
        // ====================================

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + usuario.getRol().name().toUpperCase()
                        )
                )
        );

        String nuevoToken = jwtUtil.generateToken(
                userDetails,
                usuario.getRol().name()
        );

        // ====================================
        // AJUSTES
        // ====================================

        String idioma = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(a -> a.getIdioma())
                .orElse("es");

        Boolean tema = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(a -> a.getTema())
                .orElse(false);

        // ====================================
        // RESPONSE
        // ====================================

        return CambiarRolResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rolAnterior(rolAnterior)
                .nuevoRol(nuevoRol)
                .mensaje("Rol actualizado correctamente")
                .token(nuevoToken)
                .idPsicologo(idPsicologo)
                .idPaciente(idPaciente)
                .idioma(idioma)
                .tema(tema)
                .build();
    }

    public List<UsuarioDTO> getUsuarios(String rol, String dni) {

        RolUsuario rolEnum = null;

        if (rol != null && !rol.isBlank()) {
            rolEnum = java.util.Arrays.stream(RolUsuario.values())
                    .filter(r -> r.name().equalsIgnoreCase(rol))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Rol inválido: " + rol));
        }

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

        System.out.println("TOTAL USUARIOS: " + usuarios.size());
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