package com.amani.amaniapirest.services.psicologo;

import com.amani.amaniapirest.dto.dtoPaciente.request.PsicologoRequestDTO;
import com.amani.amaniapirest.dto.dtoPsicologo.response.PsicologoSelfResponseDTO;
import com.amani.amaniapirest.dto.profile.PsicologoDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.mappers.ProfileMapper;
import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.PacientesRepository;
import com.amani.amaniapirest.repository.PsicologoRepository;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PsicologoSelfService {

    private final PsicologoRepository psicologoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacientesRepository pacientesRepository;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder; //  Usamos el encoder inyectado

    /** Obtener todos los psicólogos */
    public List<PsicologoSelfResponseDTO> findAll() {
        return psicologoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** Obtener psicólogo por id */
    public PsicologoSelfResponseDTO findById(Long idPsicologo) {
        return toResponse(getPsicologoOrThrow(idPsicologo));
    }

    /** Obtener perfil profesional por id */
    public PsicologoDTO findProfileById(Long idPsicologo) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        return profileMapper.toPsicologoDTO(
                psicologo
        );
    }

    /** Crear usuario + perfil de psicólogo completo */
    public PsicologoSelfResponseDTO create(PsicologoRequestDTO request) {

        // Validar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombrePsicologo());
        usuario.setApellido(request.getApellidoPsicologo());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // encriptar password
        usuario.setRol(RolUsuario.psicologo);
        usuario = usuarioRepository.save(usuario);

        // Crear psicólogo asociado
        Psicologo psicologo = new Psicologo();
        psicologo.setUsuario(usuario);
        mapRequestToPsicologo(psicologo, request);
        psicologo.setCreatedAt(LocalDateTime.now());
        psicologo.setUpdatedAt(LocalDateTime.now());
        psicologo = psicologoRepository.save(psicologo);

        return toResponse(psicologo);
    }

    /** Actualizar perfil de psicólogo (solo datos profesionales) */
    public PsicologoSelfResponseDTO update(Long idPsicologo, PsicologoRequestDTO request) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);

        // Actualizar datos de usuario si se envían
        Usuario usuario = psicologo.getUsuario();
        if (request.getNombrePsicologo() != null) usuario.setNombre(request.getNombrePsicologo());
        if (request.getApellidoPsicologo() != null) usuario.setApellido(request.getApellidoPsicologo());
        if (request.getEmail() != null) {
            if (!usuario.getEmail().equals(request.getEmail()) &&
                    usuarioRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
            }
            usuario.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        usuarioRepository.save(usuario);

        // Actualizar perfil profesional
        mapRequestToPsicologo(psicologo, request);
        psicologo.setUpdatedAt(LocalDateTime.now());

        return toResponse(psicologoRepository.save(psicologo));
    }

    /** Eliminar psicólogo y su usuario */
    public void delete(Long idPsicologo) {
        Psicologo psicologo = getPsicologoOrThrow(idPsicologo);
        psicologoRepository.delete(psicologo);
        // Opcional: eliminar usuario si quieres
        // usuarioRepository.delete(psicologo.getUsuario());
    }

    /** Buscar psicólogo o lanzar excepción */
    private Psicologo getPsicologoOrThrow(Long idPsicologo) {
        return psicologoRepository.findById(idPsicologo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Psicólogo no encontrado con id: " + idPsicologo));
    }

    /** Convertir a DTO de respuesta */
    private PsicologoSelfResponseDTO toResponse(Psicologo psicologo) {
        return new PsicologoSelfResponseDTO(
                psicologo.getIdPsicologo(),
                psicologo.getUsuario().getNombre(),
                psicologo.getUsuario().getApellido(),
                psicologo.getEspecialidad(),
                psicologo.getExperiencia(),
                psicologo.getDescripcion(),
                psicologo.getLicencia()
        );
    }

    /** Mapear campos del request a psicólogo */
    private void mapRequestToPsicologo(Psicologo psicologo, PsicologoRequestDTO request) {
        if (request.getEspecialidad() != null) psicologo.setEspecialidad(request.getEspecialidad());
        if (request.getExperiencia() != null) psicologo.setExperiencia(request.getExperiencia());
        if (request.getDescripcion() != null) psicologo.setDescripcion(request.getDescripcion());
        if (request.getLicencia() != null) psicologo.setLicencia(request.getLicencia());
    }
}
