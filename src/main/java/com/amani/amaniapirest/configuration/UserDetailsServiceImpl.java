package com.amani.amaniapirest.configuration;

import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de {@link UserDetailsService} que carga usuarios desde la base de datos
 * por email para su uso en el flujo de autenticación de Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca al usuario por email y construye el {@link UserDetails} con el rol prefijado
     * como {@code ROLE_<ROL>} (ej.: {@code ROLE_admin}) para que funcione con
     * {@code hasRole()} en la configuración de seguridad.
     *
     * @param email email del usuario que intenta autenticarse
     * @throws UsernameNotFoundException si no existe ningún usuario con ese email
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );
    }
}


