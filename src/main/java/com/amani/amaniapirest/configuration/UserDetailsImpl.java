package com.amani.amaniapirest.configuration;


import com.amani.amaniapirest.models.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación personalizada de UserDetails que expone el Usuario real
 * de la aplicación para poder usar su ID, rol y relaciones.
 */
@Getter
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * ID real del usuario en base de datos
     */
    public Long getIdUsuario() {
        return usuario.getIdUsuario();
    }

    /**
     * Rol del usuario (PACIENTE, PSICOLOGO, ADMIN)
     */
    public String getRol() {
        return usuario.getRol().name();
    }

    /**
     * Entidad completa del usuario (para relaciones)
     */
    public Usuario getUsuarioEntity() {
        return usuario;
    }

    /**
     * Autoridades para Spring Security
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
