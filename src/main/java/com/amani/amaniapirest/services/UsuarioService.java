package com.amani.amaniapirest.services;

import com.amani.amaniapirest.dto.dtoPaciente.request.UsuarioRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.UsuarioResponseDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.events.UsuarioRegistradoEvent;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para gestionar los usuarios del sistema.
 *
 * <p>Proporciona operaciones CRUD sobre {@link Usuario}, incluyendo el hash
 * seguro de contraseñas mediante BCrypt y la transformación a DTOs de respuesta
 * para no exponer datos sensibles.</p>
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          ApplicationEventPublisher eventPublisher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return lista de {@link UsuarioResponseDTO} con la información pública de cada usuario
     */
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca un usuario por su identificador único.
     *
     * @param idUsuario identificador del usuario a buscar
     * @return {@link UsuarioResponseDTO} con los datos del usuario encontrado
     * @throws RuntimeException si no existe un usuario con el id proporcionado
     */
    public UsuarioResponseDTO findById(Long idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        return toResponse(usuario);
    }

    /**
     * Crea un nuevo usuario hasheando su contraseña antes de persistir.
     *
     * @param request {@link UsuarioRequestDTO} con los datos del usuario a crear
     * @return {@link UsuarioResponseDTO} con los datos del usuario creado
     * @throws RuntimeException si el rol proporcionado no es válido
     */
    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(parseRol(request.getRol()));
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : Boolean.TRUE);
        usuario.setFechaRegistro(LocalDateTime.now());

        Usuario saved = usuarioRepository.save(usuario);
        eventPublisher.publishEvent(new UsuarioRegistradoEvent(this, saved.getEmail(), saved.getNombre()));
        return toResponse(saved);
    }

    /**
     * Actualiza los datos de un usuario existente.
     * Si se incluye una nueva contraseña en el request, se hashea antes de guardar.
     *
     * @param idUsuario identificador del usuario a actualizar
     * @param request   {@link UsuarioRequestDTO} con los nuevos datos del usuario
     * @return {@link UsuarioResponseDTO} con los datos actualizados
     * @throws RuntimeException si el usuario no existe o el rol proporcionado no es válido
     */
    public UsuarioResponseDTO update(Long idUsuario, UsuarioRequestDTO request) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRol(parseRol(request.getRol()));
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : usuario.getActivo());

        if (StringUtils.hasText(request.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    /**
     * Elimina el usuario con el identificador indicado.
     *
     * @param idUsuario identificador del usuario a eliminar
     * @throws RuntimeException si no existe un usuario con el id proporcionado
     */
    public void delete(Long idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        usuarioRepository.delete(usuario);
    }

    /**
     * Recupera un usuario por id o lanza excepción si no existe.
     *
     * @param idUsuario identificador del usuario
     * @return entidad {@link Usuario} encontrada
     * @throws RuntimeException si no existe un usuario con el id proporcionado
     */
    private Usuario getUsuarioOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }

    /**
     * Convierte un valor de texto al enum {@link RolUsuario} correspondiente.
     *
     * @param rol nombre del rol en texto (insensible a mayúsculas)
     * @return constante {@link RolUsuario} correspondiente
     * @throws RuntimeException si el valor no corresponde a ningún rol válido
     */
    private RolUsuario parseRol(String rol) {
        try {
            return RolUsuario.valueOf(rol.toLowerCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Rol inválido: " + rol);
        }
    }

    /**
     * Convierte una entidad {@link Usuario} en su DTO de respuesta.
     *
     * @param usuario entidad a convertir
     * @return {@link UsuarioResponseDTO} con los datos públicos del usuario
     */
    private UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().name() : null,
                usuario.getActivo(),
                usuario.getFechaRegistro()
        );
    }
}

