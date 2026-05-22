package com.amani.amaniapirest.services.serviceAdmin;


import com.amani.amaniapirest.dto.dtoAdmin.response.UsuarioAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.UsuarioRequestDTO;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de administración para gestionar todos los usuarios del sistema.
 */
@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Ejecuta la operación correspondiente a UsuarioAdminService.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene y retorna la información correspondiente.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public List<UsuarioAdminResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene y retorna la información correspondiente.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public UsuarioAdminResponseDTO findById(Long idUsuario) {
        return toResponse(getUsuario(idUsuario));
    }

    public UsuarioAdminResponseDTO create(UsuarioRequestDTO request) {

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());

        return toResponse(usuarioRepository.save(usuario));
    }

    /**
     * Actualiza la información de un registro existente.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public UsuarioAdminResponseDTO update(Long idUsuario, UsuarioRequestDTO request) {

        Usuario usuario = getUsuario(idUsuario);

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRol(request.getRol());

        return toResponse(usuarioRepository.save(usuario));
    }

    /**
     * Elimina un registro del sistema.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public void delete(Long idUsuario) {

        Usuario usuario = getUsuario(idUsuario);

        usuario.setActivo(false);
        usuario.setFechaBaja(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    private Usuario getUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private UsuarioAdminResponseDTO toResponse(Usuario usuario) {
        return new UsuarioAdminResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaRegistro(),
                usuario.getFechaBaja()
        );
    }
}
